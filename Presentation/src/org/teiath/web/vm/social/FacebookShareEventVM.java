package org.teiath.web.vm.social;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import org.apache.log4j.Logger;
import org.teiath.data.domain.act.Event;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.social.FacebookShareService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class FacebookShareEventVM
		extends BaseVM {

	static Logger log = Logger.getLogger(FacebookShareEventVM.class.getName());

	@WireVariable
	private FacebookShareService facebookShareService;

	private String post;
	private String token;

	private Event event;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {

		token = Executions.getCurrent().getParameter("token");

		try {
			StringBuilder string = new StringBuilder(Executions.getCurrent().getParameter("state"));
			string.delete(string.indexOf("?"), string.length());
			event = facebookShareService.getEventById(Integer.parseInt(string.toString()));
		} catch (ServiceException e) {
			log.error(e.getMessage());
		}

		if (event != null) {
			//TODO post content
			post = "Υπηρεσία Εύρεσης Εκπαιδευτικών και Πολιτισμικών Δράσεων Τ.Ε.Ι Αθήνας - Νεα δράση: "+event.getEventTitle() + " " + event.getEventLocation()+" -Κωδικός δράσης: "+event.getCode();
		}
	}

	@Command
	public void onPost() {
		//Facebook Post

		FacebookClient facebookClient = new DefaultFacebookClient(token);
		if (event != null) {

			FacebookType publishMessageResponse = facebookClient
					.publish("me/feed", FacebookType.class, Parameter.with("message", post));
			Messagebox.show(MessageBuilder
					.buildErrorMessage("Το μήνυμα δημοσιεύτηκε επιτυχώς στο προφίλ σας", "Facebook"), "Social Networks",
					Messagebox.OK, Messagebox.INFORMATION);
			ZKSession.sendRedirect(PageURL.ACTION_LIST);
		}
	}

	@Command
	public void onCancel() {
		ZKSession.sendRedirect(PageURL.ACTION_LIST);
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}
}
