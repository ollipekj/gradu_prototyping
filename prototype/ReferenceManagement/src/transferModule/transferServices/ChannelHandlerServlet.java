package transferModule.transferServices;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utility.parsers.JSONparser;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

public class ChannelHandlerServlet extends HttpServlet {
	
	JSONparser jsonParser = new JSONparser();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		ChannelService channelService = ChannelServiceFactory.getChannelService();

		// The channelKey can be generated in any way that you want, as long as it remains
		// unique to the user.
		String seed = req.getParameter("tokenSeed");
		String token = channelService.createChannel(seed);
		String returnMessage = jsonParser.stringsToJson("token", token);
		
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(returnMessage);
	}
}
