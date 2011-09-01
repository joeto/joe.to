package to.joe.listener;

import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;

import to.joe.J2;

public class WeatherAll extends WeatherListener {

    private final J2 j2;

    public WeatherAll(J2 j2) {
        this.j2 = j2;
    }

    @Override
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState() && !this.j2.config.world_weather_enable) {
            event.setCancelled(true);
        }
    }

}
