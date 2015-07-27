package fr.univmobile.backend.api;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.UniversityRepository;
import fr.univmobile.backend.domain.UsageStatDto;
import fr.univmobile.backend.domain.UsageStatRepository;
import fr.univmobile.backend.domain.User;

@Controller
@RequestMapping("/admin/stats")
public class UsageStatsReportController {

	private static final Log log = LogFactory
			.getLog(UsageStatsReportController.class);

	@Autowired
	UniversityRepository universityRepository;
	@Autowired
	UsageStatRepository usageStatRepository;

	@RequestMapping(method = RequestMethod.GET)
	public String get(@ModelAttribute("statsForm") StatsForm statsForm,
			HttpServletRequest request, HttpServletResponse response,
			Model model) throws IOException {

		List<University> universities;
		List<UsageStatDto> stats;
		University queriedUniversity = null;

		if (getPrincipal().isSuperAdmin()) {
			universities = universityRepository.findAllWithoutCROUS();
			queriedUniversity = statsForm.getUniversity() == null ? null : universityRepository.findOne(statsForm.getUniversity());
		} else {
			University userUniversity = universityRepository.findOne(getPrincipal().getUniversity().getId());
			universities = new ArrayList<University>(1);
			universities.add(userUniversity);
			queriedUniversity = userUniversity;
		}
		
		if (queriedUniversity == null || statsForm.getFrom() == null || statsForm.getTo() == null) {
			stats = new ArrayList<UsageStatDto>(0);
		} else {
			Calendar c = Calendar.getInstance();
			c.setTime(statsForm.getTo());
			c.add(Calendar.DATE, 1);
			c.add(Calendar.MILLISECOND, -1);
			Date to = c.getTime();
			stats = usageStatRepository.getUsageStatsByUniversity(queriedUniversity, statsForm.getFrom(), to);
		}
		
		model.addAttribute("universities", universities);
		model.addAttribute("stats", stats);

		return "usagestats_report";
	}
	
	@ModelAttribute("statsForm")
	public StatsForm getStatsFormInitialized() {
		
		StatsForm form = new StatsForm();
		Date today = new Date();
		
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DATE, -30);

		form.setFrom(c.getTime());
		form.setTo(today);
		
		return form;
	}

	public class StatsForm {
		private Long university;
		@DateTimeFormat(pattern = "dd-MM-yyyy")
		private Date from;
		@DateTimeFormat(pattern = "dd-MM-yyyy")
		private Date to;

		public Long getUniversity() {
			return university;
		}

		public void setUniversity(Long university) {
			this.university = university;
		}

		public Date getFrom() {
			return from;
		}

		public void setFrom(Date from) {
			this.from = from;
		}

		public Date getTo() {
			return to;
		}

		public void setTo(Date to) {
			this.to = to;
		}
	}

	private User getPrincipal() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth == null ? null : (User) auth.getPrincipal();
	}
}
