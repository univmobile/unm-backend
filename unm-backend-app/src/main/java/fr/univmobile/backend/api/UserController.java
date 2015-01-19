package fr.univmobile.backend.api;

import fr.univmobile.backend.hateoas.assembler.UserResourceAssembler;
import fr.univmobile.backend.hateoas.resource.UserResource;
import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserResourceAssembler userResourceAssembler;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public UserResource index(@RequestParam(value = "id") Integer id) {
        User u = userRepository.findOne(new Long(id));
        UserResource user = null;
        if (u != null) {
            user = userResourceAssembler.toResource(u);
        }
        return user;
    }


}