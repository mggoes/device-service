db = db.getSiblingDB("device_db");

db.createUser({
    user: "device_service",
    pwd: "device@app",
    roles: [
        {
            role: 'readWrite',
            db: 'device_db'
        }
    ]
});

db.createCollection("devices");
