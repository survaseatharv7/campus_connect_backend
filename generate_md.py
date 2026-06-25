import sys
import re

with open('api_dump.txt', 'r', encoding='utf-8') as f:
    lines = f.readlines()

dtos = {}
controllers = []

mode = None
current_dto = None

for line in lines:
    line = line.strip('\n')
    if line == '=== DTOs ===':
        mode = 'dtos'
        continue
    elif line == '=== Controllers ===':
        mode = 'controllers'
        continue
    
    if mode == 'dtos':
        if line.startswith('DTO '):
            current_dto = line[4:-1]
            dtos[current_dto] = []
        elif line.startswith('Enum '):
            parts = line[5:].split(':')
            name = parts[0].strip()
            values = parts[1].strip()
            dtos[name] = f"**Enum Values:** `{values}`"
        elif current_dto and line.startswith('  '):
            dtos[current_dto].append(line.strip())
            
    elif mode == 'controllers':
        if line.startswith('Controller: '):
            match = re.match(r'Controller: (\w+\.java) \(Base: (.*?)\)', line)
            if match:
                controllers.append({
                    'name': match.group(1).replace('.java', ''),
                    'base_path': match.group(2),
                    'endpoints': []
                })
        elif '->' in line:
            parts = line.split('->')
            if len(parts) == 3:
                meth_path = parts[0].strip().split(' ')
                method = meth_path[0]
                path = meth_path[1] if len(meth_path) > 1 else ''
                body = parts[1].replace('Body:', '').strip()
                returns = parts[2].replace('Returns:', '').strip()
                controllers[-1]['endpoints'].append({
                    'method': method,
                    'path': path,
                    'body': body,
                    'returns': returns
                })

def format_dto_recursive(dto_name, indent=""):
    out = ""
    dto_name = dto_name.strip()
    # Parse generic types
    if '<' in dto_name and dto_name.endswith('>'):
        match = re.match(r'(\w+)<(.*)>', dto_name)
        if match:
            container = match.group(1)
            inner = match.group(2)
            out += f"{indent}- **{container}** containing:\n"
            out += format_dto_recursive(inner, indent + "  ")
            return out
    
    if dto_name in dtos:
        dto_content = dtos[dto_name]
        if isinstance(dto_content, str):
            out += f"{indent}- {dto_content}\n"
        else:
            for field in dto_content:
                parts = field.split(' ')
                if len(parts) >= 2:
                    ftype = ' '.join(parts[:-1])
                    fname = parts[-1]
                    out += f"{indent}- `{fname}` (*{ftype}*)\n"
    else:
        out += f"{indent}- Primitive/Object (`{dto_name}`)\n"
    return out

with open('API_ENDPOINTS.md', 'w', encoding='utf-8') as f:
    f.write("# CampusNexus API Endpoints\n\n")
    f.write("> Complete backend endpoints documentation with expected request bodies and responses for frontend integration.\n\n")
    
    for c in controllers:
        f.write(f"## {c['name']}\n")
        f.write(f"**Base Path:** `{c['base_path']}`\n\n")
        for e in c['endpoints']:
            f.write(f"### {e['method']} `{e['path']}`\n")
            f.write("#### Request Body\n")
            if e['body'] == 'None' or e['body'] == '':
                f.write("No request body.\n")
            else:
                f.write(f"**Type:** `{e['body']}`\n\n")
                f.write(format_dto_recursive(e['body']))
            f.write("\n#### Response\n")
            f.write(f"**Type:** `{e['returns']}`\n\n")
            f.write(format_dto_recursive(e['returns']))
            f.write("\n---\n\n")
