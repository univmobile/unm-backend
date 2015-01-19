package fr.univmobile.backend.hateoas.assembler;

import fr.univmobile.backend.api.CategoryController;
import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.hateoas.resource.CategoryResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class CategoryResourceAssembler extends ResourceAssemblerSupport<Category, CategoryResource> {

    private static final String CATEGORIES_PATH = "/api/categories/";
    private static final String POIS_PATH = "/pois";
    private static final String PARENT_PATH = "/parent";
    private static final String UPDATED_BY_PATH = "/updatedBy";
    private static final String CHILDREN_PATH = "/children";
    private static final String CREATED_BY_PATH = "/createdBy";

    @Value("${baseURL}")
    private String baseUrl;

    public CategoryResourceAssembler() {
        super(CategoryController.class, CategoryResource.class);
    }

    @Override
    public CategoryResource toResource(Category category) {
        CategoryResource categoryResource = null;
        if (category != null) {
            categoryResource = new CategoryResource();
            categoryResource.name = category.getName();
            categoryResource.description = category.getDescription();
            categoryResource.active = category.isActive();
            categoryResource.legacyIds = category.getLegacyIds();

            categoryResource.add(new Link(baseUrl + CATEGORIES_PATH + category.getId()));
            categoryResource.add(new Link(baseUrl + CATEGORIES_PATH + category.getId() + POIS_PATH, "pois"));
            categoryResource.add(new Link(baseUrl + CATEGORIES_PATH + category.getId() + PARENT_PATH, "parent"));
            categoryResource.add(new Link(baseUrl + CATEGORIES_PATH + category.getId() + UPDATED_BY_PATH, "updatedBy"));
            categoryResource.add(new Link(baseUrl + CATEGORIES_PATH + category.getId() + CHILDREN_PATH, "children"));
            categoryResource.add(new Link(baseUrl + CATEGORIES_PATH + category.getId() + CREATED_BY_PATH, "createdBy"));
        }
        return categoryResource;
    }
}