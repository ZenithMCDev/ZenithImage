package fr.zenith.image.manipulation.defaults;

import fr.zenith.image.ImageRender;
import fr.zenith.image.manipulation.defaults.data.ClickedImageData;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class ModifiedClickableImage extends ModifiedImage {

    private final Consumer<ClickedImageData> onClick;

    public ModifiedClickableImage(ImageRender plugin, BufferedImage image, Consumer<ClickedImageData> onClick) {
        super(plugin, image);
        this.onClick = onClick;
    }

    public ModifiedClickableImage(ImageRender plugin, int width, int height, Consumer<ClickedImageData> onClick) {
        this(plugin, new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), onClick);
    }

    public void onClick(ClickedImageData data) {
        this.onClick.accept(data);
    }
}
