# Animatronic effect
This effect is used to play an animatronic at a certain location. See the animatronic documentation for more details on how to set up animatronics.

## Configuration format
```json lines
// inside a keyframes list
{
  "time": 1,
  "type": "animatronic",
  "settings": {
    "name": "animatronic_name", // the name of the animatronic to play, without file extension
    "animation": "animation_name", // the name of the animation to play
    "loop": false, // whether the animation should loop
    "base_location": { // the location of the animatronic
      "world": "world",
      "x": -17.5,
      "y": -4.0,
      "z": -301.7
    },
    "base_rotation": { // the rotation of the animatronic, will be added to the default/animated rotation
      "pitch": 0.0,
      "yaw": 0.0,
      "roll": 0.0
    },
    "forwards": true // if set to true, the animatronic will stay until the effect has ended
  }
}
```