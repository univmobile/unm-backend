package fr.univmobile.backend.hateoas.processor;

import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.RestoMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

public class PoiResourceProcessor implements ResourceProcessor<Resource<Poi>> {

    private static String RESTO_MENUES_FOR_POI_PATH = "api/restoMenus/search/findRestoMenuForPoi?poiId=";

    @Value("${baseURL}")
    private String baseUrl;

    @Autowired
    private RestoMenuRepository restoMenuRepository;

    @Override
    public Resource<Poi> process(Resource<Poi> resource) {
        Poi poi = resource.getContent();
        if (restoMenuRepository.CountRestoMenuesForPoi(poi) > 0){
            resource.add(new Link(baseUrl + RESTO_MENUES_FOR_POI_PATH + poi.getId(), "restoMenus"));
        }
        return resource;
    }
}
