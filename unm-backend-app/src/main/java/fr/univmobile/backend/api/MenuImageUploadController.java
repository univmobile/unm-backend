package fr.univmobile.backend.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import fr.univmobile.backend.domain.Category;
import fr.univmobile.backend.domain.CategoryRepository;
import fr.univmobile.backend.domain.Comment;
import fr.univmobile.backend.domain.CommentRepository;
import fr.univmobile.backend.domain.ImageMap;
import fr.univmobile.backend.domain.ImageMapRepository;
import fr.univmobile.backend.domain.Poi;
import fr.univmobile.backend.domain.PoiRepository;
import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.UniversityRepository;
import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;
import fr.univmobile.backend.helpers.QrCode;

@Controller
@RequestMapping("/admin/menu")
public class MenuImageUploadController {

	private static final Log log = LogFactory.getLog(MenuImageUploadController.class);
	
	@Value("${baseURL}")
	private String baseUrl;
	@Value("${menuImagesBaseUrl}")
	private String menuImagesBaseUrl;
	@Value("${menuImagesBaseDir}")
	private String menuImagesBaseDir;
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User currentUser = getCurrentUser(request);
		
		if (currentUser == null || !(currentUser.isSuperAdmin() || currentUser.isAdmin())) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		
		Map<String, String> result = new HashMap<String, String>();
		
        try {
        	String fileName = handleFileUpload(file);
        	if (fileName == null) {
            	log.error(String.format("Upload failed for %s", file.getOriginalFilename()));
            	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            	return null;
        	}
        	result.put("url", String.format("%s/%s", menuImagesBaseUrl, fileName));
        	return result;
        } catch (Exception e) {
        	log.error(String.format("There was an error persisting an uploaded file: %s", file.getOriginalFilename()));
        	log.error(e);
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        	return null;
        }
	}

	private String handleFileUpload(MultipartFile file) {
		if (!file.isEmpty()) {
            try {
            	String fileName = String.format("upload_%s_%s", System.currentTimeMillis(), file.getOriginalFilename());
            	String filePath = Paths.get(menuImagesBaseDir, fileName).toString();
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                stream.write(bytes);
                stream.close();
                return fileName;
            } catch (Exception e) {
            	log.error(String.format("There was an error while uploading file"));
            	log.error(e);
            	return null;
            }
        } else {
            log.warn(String.format("Image map uploading failed for because file is empty"));
        	return null;
        }
	}
	
	private User getCurrentUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute("user");
	}
}
