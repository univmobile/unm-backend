package fr.univmobile.backend.api;

import fr.univmobile.backend.domain.CommentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
public class CommentController {

	@Autowired
	CommentRepository commentRepository;

	@Transactional
	@RequestMapping(value = { "/changeStatus" }, method = RequestMethod.PATCH)
	public boolean changeStatus(@RequestParam("commentId") Long commentId, @RequestParam("status") Boolean status) {
		return commentRepository.setStatus(commentId, status) > 0;
	}
	
}