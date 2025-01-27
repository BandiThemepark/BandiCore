# Spatial Audio effect
The spatial audio effect can be used to play a spatial sound at a certain location. These sounds are played through the AudioServer.

## Configuration format
```json
{
    "time": 1,
    "type": "spatial_audio",
    "settings": {
        "source": "f2c3533b-efdb-4f57-a433-d866f75391ca", // the UUID of the audio source to play, can be found in the crew panel
        "innerRange": 4.0, // in this range, the audio is heard at full volume
        "outerRange": 20.0, // in this range, the volume interpolates from 100 to 0, based on the distance
        "looping": false, // whether the audio should loop
        "location": { // the location of the audio
            "world": "world",
            "x": 36.5,
            "y": 0.0,
            "z": -160.0
        }
    }
}
```