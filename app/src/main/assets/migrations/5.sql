ALTER TABLE Projects ADD COLUMN ProjectEmail TEXT;

ALTER TABLE WorkingOrders ADD COLUMN LatestUplink TEXT;
ALTER TABLE WorkingOrders ADD COLUMN LateseDownlink TEXT;