# Parkours
Parkours are a feature that allow you to create timed parkours for players to complete. They can have leaderboards, and feature multiple protections to prevent cheaters.

## Creating a parkour
A parkour requires three regions to be created:

1. **The start region** Players stepping from this region into the core region will start the parkour
2. **The core region** This is the core area where the parkour is in
3. **The end region** Players stepping from the core region into this region will finish the parkour

You need to create regions for this and give them areas first. Then you can move on to the configuration.

## Configuration
The configuration of a parkour is done in the `parkours.json` file, found in the `plugins/BandiCore` folder. This file is located in the `plugins/BandiCore` folder. The configuration is done in the following format:

```json lines
{
    "parkours": [ // a list of all parkours
        {
            "id": "warehouse", // the id of the parkour
            "displayName": "Warehouse", // the display name of the parkour, used for messages and the leader board
            "startRegionId": "warehousestart", // the id of the start region
            "coreRegionId": "warehousecore", // the id of the core region
            "endRegionId": "warehouseend", // the id of the end region
            "leaderboards": [ // a list of all spawned leaderboards
                {
                    "world": "world",
                    "x": -125.0,
                    "y": 3.0,
                    "z": -1014.5,
                    "yaw": -90.0
                },
                ... // more locations
            ]
        },
        ... // more parkours
    ]
}
```

After adding the parkour, you can run `/bandi reload parkours` to reload the parkours. The parkour will now be available for players to play.