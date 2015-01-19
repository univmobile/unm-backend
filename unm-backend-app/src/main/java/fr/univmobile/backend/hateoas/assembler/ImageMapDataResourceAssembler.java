package fr.univmobile.backend.hateoas.assembler;

import fr.univmobile.backend.ImageMapController;
import fr.univmobile.backend.hateoas.resource.ImageMapDataResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import fr.univmobile.backend.api.ImageMapController.ImageMapData;

public class ImageMapDataResourceAssembler extends ResourceAssemblerSupport<ImageMapData, ImageMapDataResource> {

    @Autowired
    private PoiResourceAssembler poiResourceAssembler;

    @Autowired
    private ImageMapResourceAssembler imageMapResourceAssembler;

    public ImageMapDataResourceAssembler() {
        super(ImageMapController.class, ImageMapDataResource.class);
    }

    @Override
    public ImageMapDataResource toResource(ImageMapData imageMapData) {
        ImageMapDataResource imageMapDataResource = null;
        if (imageMapData != null) {
            imageMapDataResource = new ImageMapDataResource();
            imageMapDataResource.imageMap = imageMapResourceAssembler.toResource(imageMapData.getImageMap());
            imageMapDataResource.selectedPoi = poiResourceAssembler.toResource(imageMapData.getSelectedPoi());
        }
        return imageMapDataResource;
    }
}