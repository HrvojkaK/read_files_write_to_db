package com.evolva.santa.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.evolva.santa.model.MoneyCount;
import com.evolva.santa.service.FileService;
import com.evolva.santa.service.MoneyCountService;

@Controller
public class HomeController {

	@Autowired
    private FileService fileService;
	
	@Autowired
	private MoneyCountService moneyCountService;
	
	
	public HomeController() {}
	
	@GetMapping("")
	public String homePage() {

		return "index";
	}
	
	
	@GetMapping("/fillDb")
	public String fillDb() {
		try {
            // Call the service method to read the files and insert into db
            return fileService.fillDatabase("C:/data"); //redirects to report page
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }		
	}
	
	
	@GetMapping("/report")
	public String report(Model theModel) {

		List<MoneyCount> c = moneyCountService.getCount();
		theModel.addAttribute("count", c);
		
		return "calculated";
	}
	
	

}
