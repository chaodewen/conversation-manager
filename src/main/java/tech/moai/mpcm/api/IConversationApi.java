package tech.moai.mpcm.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/conversations")
public interface IConversationApi {
	/**
	 * 返回所有对话
	 */
	@GET
	@Path("all")
	@Produces("application/json")
	public Response getAllConversations(@QueryParam("from") String from
			, @QueryParam("to") String to);
	/**
	 * Retrieve messages from your account in bulk.
	 * 只返回非Bot的消息
	 * @param key
	 * @param botID Optionally, you may specify a bot ID to retrieve messages sent/received by that specific bot.
	 * @param botType Optionally, specify a single medium to filter messages by. (fb, slack, webchat, email, sms)
	 * @param session Optionally, you may filter conversations by session ID. To retrieve a session ID, first make a request to this API without specifying a session and locate a message from your desired session, where you'll find a session variable.
	 * @param module Optionally, you may filter conversations by module ID.
	 * @param direction in or out - do not specify anything if you wish to include both inbound and outbound messages
	 * @param archived If false, it will only return unarchived messages.
	 */
	@GET
	@Path("")
	@Produces("application/json")
	public Response getConversations(@QueryParam("key") String key
			, @QueryParam("botID") int botID
			, @QueryParam("botType") String botType
			, @QueryParam("session") String session
			, @QueryParam("module") int module
			, @QueryParam("direction") String direction
			, @QueryParam("archived") boolean archived);
	/**
	 * Initiate or continue a conversation from a human with one of your bots.
	 * @param botID Specifies the bot ID you would like to communicate with.
	 * @param msg The human message to the bot.
	 * @param session The session ID for this conversation. Can be any string (such as an IP address hash or session ID from your own system).
	 * @param key
	 */
	@GET
	@Path("botMessages")
	@Produces("application/json")
	public Response messageBot(@QueryParam("botID") int botID
			, @QueryParam("msg") String msg
			, @QueryParam("session") String session
			, @QueryParam("key") String key);
	/**
	 * Initiate or continue a conversation with a human from one of your bots, outside of the normal conversation flow you've defined.
	 * @param to Depending on the bot type: an human's SMS phone number, email, Facebook Recipient ID or session ID. See below for more information.
	 * @param botID The bot ID to send from.
	 * @param msg The message to send to the human.
	 * @param key
	 */
	@GET
	@Path("humanMessages")
	@Produces("application/json")
	public Response messageHuman(@QueryParam("to") String to
			, @QueryParam("botID") int botID
			, @QueryParam("msg") String msg
			, @QueryParam("key") String key);
}