package com.tinhnv.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.Gson;
import com.tinhnv.model.account.SessionUser;
import com.tinhnv.service.AccountService;
import com.tinhnv.setting.AccountConfig;

@Controller
@RequestMapping(value={"/greeting", "/"})
public class GreetingController {
	
	@Autowired
	AccountService account;
	
	@Autowired
	AccountConfig accountConfig;
	
	Gson gson = new Gson();
	
	@RequestMapping(value={"/dang-xuat"})
	public String logoutAction(HttpSession session) {
		session.invalidate();
		return "redirect:/greeting/dang-nhap";
	}
	@RequestMapping(value={"/dang-ky-tai-khoan"}, method=RequestMethod.GET)
	public String registerPage() {
		return "register";
	}
	@RequestMapping(value={"/register_result"}, method=RequestMethod.GET)
	public String registerResultPage() {
		return "resultRegister";
	}
	@RequestMapping(value={"/dang-nhap"}, method=RequestMethod.GET)
	public String loginPage() {
		return "login";
	}
	@RequestMapping(value={"/quen-mat-khau"}, method=RequestMethod.GET)
	public String forgotPasswordPage() {
		return "forgotPassword";
	}
	@RequestMapping(value={"/doi-mat-khau"}, method=RequestMethod.GET)
	public String changePasswordPage() {
		return "changePassword";
	}
	@RequestMapping(value={"/home", "/"}, method=RequestMethod.GET)
	public String homePage() {
		return "home";
	}
	@RequestMapping(value={"/trang-chinh"}, method=RequestMethod.GET)
	public String homePage(HttpSession session, HttpServletRequest request) {
		if(session.getAttribute("user") == null) {
			request.setAttribute("message", "H???t phi??n, vui l??ng ????ng nh???p l???i!");
			return "login";
		}
		SessionUser user = (SessionUser) session.getAttribute("user");
		if(user.getRole().equals(accountConfig.ADMIN)) {
			return "redirect:/admin/tong-quan";
		} else if(user.getRole().equals(accountConfig.USER)) {
			return "redirect:/greeting/home";
		}
		request.setAttribute("message", "L???i ph??n quy???n ng?????i d??ng");
		return "error";
	}
	
	@RequestMapping(value={"/kiem-tra-email"}, method=RequestMethod.POST)
	public void isNewEmail(@RequestParam(name="email") String email, HttpServletResponse response) throws IOException {
		boolean result = account.isNewEmail(email);
		String json = gson.toJson(result);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	
	@RequestMapping(value={"/dang-ky-tai-khoan"}, method=RequestMethod.POST)
	public RedirectView registerResult(HttpServletRequest request) {
		boolean result = account.createAccountFromRegister(request);
		RedirectView view = new RedirectView("https://love-and-care.herokuapp.com/greeting/register_result");
		view.addStaticAttribute("result", result);
		return view;
	}
	
	@RequestMapping(value={"/dang-nhap"}, method=RequestMethod.POST)
	public ModelAndView loginResult(HttpServletRequest request, HttpServletResponse response) {
		String remember = request.getParameter("remember");
		String email = request.getParameter("email");
		String password = "";
		HttpSession session = request.getSession(true);
		SessionUser user = account.checkAndGetAccount(request);
		session.setAttribute("user", user);
		session.setMaxInactiveInterval(60*60*24);
		String page = "home";
		switch(user.getId()) {
			case 0:
				//no email found
				request.setAttribute("message", "Email ho???c m???t kh???u kh??ng ch??nh x??c!");
				request.setAttribute("email", email);
				page = "login";
				break;
			case -1:
				//wrong password
				request.setAttribute("message", "M???t kh???u kh??ng ????ng! :(");
				request.setAttribute("email", email);
				page = "login";
				break;
			case -2:
				//null user
				request.setAttribute("message", "H???t phi??n, vui l??ng ????ng nh???p l???i");
				page = "login";
				break;
			default:
				//login successfully
				if(remember != null && remember.equals("on")) {
					Cookie emailRemember = new Cookie("emailRemember", email);
					emailRemember.setMaxAge(60*60*24);
					response.addCookie(emailRemember);
				}
				if(!user.isVerified()) {
					//new account => change password
					request.setAttribute("message", "H??y quy???t ?????nh m???t kh???u m???i c???a b???n :)");
					page = "changePassword";
				} else {
					//locked account
					if(!user.isActive()) {
						request.setAttribute("message", "T??i kho???n c???a b???n ??ang b??? kh??a! H??y li??n h??? v???i ng?????i qu???n tr???.");
						page = "login";
					} else {
						if(remember != null && remember.equals("on")) {
							password = request.getParameter("password");
							Cookie passwordRemember = new Cookie("passwordRemember", password);
							passwordRemember.setMaxAge(60*60*24);
							response.addCookie(passwordRemember);
						}
						//to home page
						if(user.getRole().equals(accountConfig.ADMIN)) {
							//admin dashboard
							page = "redirect:/admin/tong-quan";
						} else if(user.getRole().equals(accountConfig.USER)) {
							//user home page
							page = "redirect:/greeting/home";
						}
					}
				}
		}
		return new ModelAndView(page);
	}
	
	@RequestMapping(value={"doi-mat-khau"}, method=RequestMethod.POST)
	public String changePassResult(HttpSession session, HttpServletRequest request) throws IllegalAccessException {
		if(session.getAttribute("user") == null) {
			request.setAttribute("message", "H???t phi??n, vui l??ng ????ng nh???p l???i!");
			return "login";
		}
		SessionUser user = (SessionUser) session.getAttribute("user");
		
		String message = account.changeAccountPassword(request);
		if(message == null) {
			if(!user.isActive()) {
				request.setAttribute("message", "T??i kho???n c???a b???n ??ang b??? kh??a! H??y li??n h??? v???i ng?????i qu???n tr???.");
				return "login";
			} else {
				if(user.getRole().equals(accountConfig.ADMIN)) {
					return "redirect:/admin/tong-quan";
				} else if(user.getRole().equals(accountConfig.USER)) {
					return "redirect:/greeting/home";
				}
			}
			request.setAttribute("message", "L???i ph??n quy???n ng?????i d??ng");
			return "error";
		}
		
		request.setAttribute("message", message);
		return "changePassword";
	}
	
	@RequestMapping(value={"/lay-lai-mat-khau"}, method=RequestMethod.POST)
	public RedirectView resetPass(@RequestParam(name="email") String email) {
		RedirectView view = null;
		if(account.resetAccountPassword(email)) {
			view = new RedirectView("https://love-and-care.herokuapp.com/greeting/dang-nhap");
			view.addStaticAttribute("message", "Ch??ng t??i ???? g???i m???t kh???u m???i ?????n email c???a b???n");
			view.addStaticAttribute("email", email);
		} else {
			view = new RedirectView("https://love-and-care.herokuapp.com/greeting/quen-mat-khau");
			view.addStaticAttribute("message", "H??y ch???c ch???n r???ng b???n ???? ????ng k?? t??i kho???n b???ng email n??y");
			view.addStaticAttribute("email", email);
		}
		return view;
	}

}
