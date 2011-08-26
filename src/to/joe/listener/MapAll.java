package to.joe.listener;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.bukkit.entity.Player;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import to.joe.J2;

public class MapAll extends ServerListener {

    private final J2 j2;
    private final MapRenderer mapRenderer = new MapRend();
    private BufferedImage image = null;
    private final MinecraftFont font = new MinecraftFont();

    public MapAll(J2 j2) {
        this.j2 = j2;
        try {
            this.image = ImageIO.read(new File("logo.png"));
        } catch (final IOException e) {
        }
    }

    @Override
    public void onMapInitialize(MapInitializeEvent event) {
        if (this.j2.config.general_server_number == 2) {
            final MapView mapView = event.getMap();
            final List<MapRenderer> rendererList = mapView.getRenderers();
            for (final MapRenderer renderer : rendererList) {
                mapView.removeRenderer(renderer);
            }
            mapView.addRenderer(this.mapRenderer);
        }
    }

    private class MapRend extends MapRenderer {

        public MapRend() {
            super(true);
        }

        @Override
        public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
            if (MapAll.this.image != null) {
                mapCanvas.drawImage(0, 0, MapAll.this.image);
                mapCanvas.drawText(20, 15, MapAll.this.font, "mc2.joe.to");
                mapCanvas.drawText(0, 30, MapAll.this.font, "READ, " + player.getName());
                mapCanvas.drawText(0, 50, MapAll.this.font, "Free items: /i");
                mapCanvas.drawText(10, 60, MapAll.this.font, "Example: /i diamond 10");
                mapCanvas.drawText(0, 80, MapAll.this.font, "For more information");
                mapCanvas.drawText(0, 90, MapAll.this.font, "you MUST read the output from");
                mapCanvas.drawText(20, 100, MapAll.this.font, "/intro");
            }
        }

    }
}
