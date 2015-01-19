package fr.univmobile.backend.api;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.backend.hateoas.assembler.CategoryResourceAssembler;
import fr.univmobile.backend.hateoas.resource.CategoryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryResourceAssembler categoryResourceAssembler;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public CategoryResource index(@RequestParam(value = "id") Integer id) {
        Category u = categoryRepository.findOne(new Long(id));
        CategoryResource user = null;
        if (u != null) {
            user = categoryResourceAssembler.toResource(u);
        }
        return user;
    }
}
