# Effects

Effects allow you to add visual effects to the park. You can play these on a loop, when a coaster passes a certain point or using other ways. You can create things like fountains, animatronics and other visual effects.

## Creating an effect
To create an effect, navigate to the `plugins/BandiCore/effects` folder. Here, you'll create a new `JSON` file. This file will contain the effect's configuration. The general naming convention is `snake_case`, so an example file name would be `my_effect.json`.

This is how you configure an effect:

```json
{
    "duration": 1, // the duration of the effect in milliseconds
    "loop": false, // whether the effect should loop
    "forwards": true, // if set to true, the effect will never despawn. Can be used to make animatronics stay after they are done animating for example
    "keyframes": [ // a list of all the keyframes in the effect
        {
            "time": 1, // the time in milliseconds that this keyframe should played at
            "type": "your-type", // see the list of types below
            "settings": {
                // settings for the effect type, see the page of your specific type
            },
            "debug": false // used by some effects to show debug information
        },
        ... // more keyframes
    ]
}
```

## Playing an effect
To play an effect, you can use the `/effect` command, like this:

```
/effect play my_effect
```

You do not have to add the file extension to the effect name. This will trigger the effect

## Playing an effect on server start
Some effects need to always play when the server has started. Imagine for example a fountain, which is usually a looping particle effect. To play an effect on server start, you can use the `server-start-effects` section in the `config.json` file. Here's an example:

```json
// config.json
{
    "server-start-effects": [
        "my_effect",
        ...
    ]
}
```

You can reload this configuration using `/bandi reload server-start-effects`. This is both useful when you add and remove effects from the list, but will also reload the effect if you make individual changes to the config.

## Effect track trigger
There is also a track trigger that can be used to play effects when a vehicle passes a certain point on a track. Once you are in the track editor, go to the trigger editor.

Here, you can add a new trigger. The trigger type should be `effect`. The metadata format is as follows:

```
EFFECT_NAME, ONLY_FOR_RIDERS (true/false)
```

Example configuration for an effect named `my_effect` that should only be played for riders:

```
my_effect, true
```

See the track documentation for more details on how to manage triggers.

## Effect types
There are multiple types of effects that you can use. Here's a list of all the types currently available:

- [Particle](./particle.md)
- [Animatronic](./animatronic.md)
- [Spatial Audio](./spatial_audio.md)
- [Dark overlay](./dark_overlay.md)