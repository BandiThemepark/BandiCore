# Dark overlay effect
This effect can be used to display a transparent dark overlay on the screen. This is a way to override brightness, useful for indoor scenes or attractions.

## Configuration format
```json
{
  "time": 1,
  "type": "dark_overlay",
  "settings": {
    "stay_time": 7920, // the time in milliseconds that the overlay should stay
    "fade_in_time": 40, // the time in milliseconds that the overlay should fade in
    "fade_out_time": 40 // the time in milliseconds that the overlay should fade out
  }
}
```