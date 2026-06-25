-- ============================================================
--  CampusNexus — Event Module: Legacy Price Normalization
--  Issue #1 Fix: Free Event Price
-- ============================================================
--
--  Problem:  Legacy seed data stored ticket_price = -1 to represent
--            free events. The standardized model is:
--              0           → FREE event
--              > 0         → Paid event
--              negative    → INVALID (must not exist)
--
--  Fix:      Normalize all negative ticket_price values to 0.
--
--  Safety:   This UPDATE only touches rows where ticket_price < 0.
--            Paid events (ticket_price > 0) and already-free events
--            (ticket_price = 0 or NULL) are untouched.
--
--  Idempotent: Running this script multiple times is safe.
--
--  Run BEFORE deploying the new application version.
-- ============================================================

BEGIN;

-- Show affected rows before update (for audit/logging)
SELECT
    id,
    title,
    ticket_price AS old_price,
    0            AS new_price
FROM events
WHERE ticket_price < 0;

-- Normalize negative prices to 0 (FREE)
UPDATE events
SET ticket_price = 0
WHERE ticket_price < 0;

-- Confirm result
DO $$
DECLARE
    remaining_negatives INTEGER;
BEGIN
    SELECT COUNT(*) INTO remaining_negatives FROM events WHERE ticket_price < 0;
    IF remaining_negatives > 0 THEN
        RAISE EXCEPTION 'Migration failed: % row(s) still have negative ticket_price', remaining_negatives;
    END IF;
    RAISE NOTICE 'Migration complete: all ticket_price values are now >= 0';
END $$;

COMMIT;
