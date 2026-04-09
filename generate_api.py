import os, re

controller_dir = 'src/main/java/com/campusnexus/controller'
dto_dir = 'src/main/java/com/campusnexus/dto'

def parse_java_type(t):
    return t.strip()

dtos = {}
for root, _, files in os.walk(dto_dir):
    for f in files:
        if f.endswith('.java'):
            with open(os.path.join(root, f), 'r', encoding='utf-8') as file:
                content = file.read()
                
                # Check for class or record
                class_match = re.search(r'public\s+(?:class|record|enum)\s+(\w+)', content)
                if not class_match:
                    continue
                class_name = class_match.group(1)
                
                # If enum, get values
                if 'enum ' in content:
                    values = re.findall(r'^\s*([A-Z_]+)[,\;]', content, re.MULTILINE)
                    dtos[class_name] = {'type': 'enum', 'values': values}
                    continue
                    
                # Fields
                fields = []
                for match in re.finditer(r'private\s+([\w<>,\s\[\]\?]+)\s+(\w+);', content):
                    fields.append({'type': parse_java_type(match.group(1)), 'name': match.group(2)})
                
                # If record, fields are in constructor
                if 'record ' in content:
                    record_match = re.search(r'record\s+\w+\s*\((.*?)\)', content, re.DOTALL)
                    if record_match:
                        args = record_match.group(1).split(',')
                        for arg in args:
                            parts = arg.strip().split()
                            if len(parts) >= 2:
                                # handle annotations
                                t = parts[-2]
                                n = parts[-1]
                                fields.append({'type': parse_java_type(t), 'name': n})

                dtos[class_name] = {'type': 'class', 'fields': fields}

with open('api_dump.txt', 'w', encoding='utf-8') as out:
    out.write('=== DTOs ===\n')
    for k, v in dtos.items():
        if v['type'] == 'enum':
            out.write(f'Enum {k}: {", ".join(v["values"])}\n')
        else:
            out.write(f'DTO {k}:\n')
            for f in v['fields']:
                out.write(f'  {f["type"]} {f["name"]}\n')

    out.write('\n=== Controllers ===\n')
    for root, _, files in os.walk(controller_dir):
        for f in files:
            if f.endswith('.java'):
                with open(os.path.join(root, f), 'r', encoding='utf-8') as file:
                    content = file.read()
                    base_path = ''
                    req_mapping = re.search(r'@RequestMapping\([\"\'\s]*([^\)\"\']+)', content)
                    if req_mapping:
                        base_path = req_mapping.group(1).strip()
                    out.write(f'\nController: {f} (Base: {base_path})\n')
                    
                    # matches @PostMapping("/login") public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req)
                    methods = re.findall(r'@(Get|Post|Put|Delete|Patch)Mapping\s*\(\s*["\'](.*?)["\'].*?\).*?public\s+ResponseEntity<\s*([\w<>,\.\s\?\[\]]+)\s*>\s+(\w+)\s*\((.*?)\)', content, re.DOTALL)
                    
                    # some methods might not have path like @GetMapping
                    methods_no_path = re.findall(r'@(Get|Post|Put|Delete|Patch)Mapping\s*(?:\(\s*\))?\s+.*?public\s+ResponseEntity<\s*([\w<>,\.\s\?\[\]]+)\s*>\s+(\w+)\s*\((.*?)\)', content, re.DOTALL)
                    
                    all_methods = []
                    for m in methods:
                         all_methods.append((m[0], m[1], m[2], m[3], m[4]))
                    for m in methods_no_path:
                         # Ensure we don't double count
                         if m[2] not in [x[3] for x in all_methods]:  # check method name
                             all_methods.append((m[0], '', m[1], m[2], m[3]))

                    for m in all_methods:
                        http_method = m[0].upper()
                        path = m[1].strip()
                        full_path = (base_path + path).replace('//', '/')
                        return_type = m[2].strip()
                        method_name = m[3]
                        args = m[4]
                        
                        body = 'None'
                        req_body_match = re.search(r'@RequestBody(?:\s+@Valid)?\s+([\w<>\[\]]+)\s+', args)
                        if req_body_match:
                            body = req_body_match.group(1)
                            
                        out.write(f'{http_method} {full_path} -> Body: {body} -> Returns: {return_type}\n')
