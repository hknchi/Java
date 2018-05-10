package examples;

import java.io.IOException;

import twitter4j.IDs;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Examples {
	private static String OAUTH_CONSUMER_KEY = "Private Key";
	private static String OAUTH_CONSUMER_SECRET = "Private Key";
	private static String OAUTH_ACCESS_TOKEN = "Private Key";
	private static String OAUTH_ACCESS_TOKEN_SECRET = "Private Key";
	private static ConfigurationBuilder CONFIGURATION_BUILDER;
	private static TwitterStream TWITTER_STREAM;
	private static TwitterFactory TWITTER_FACTORY;

	
	public static void main(String args[]) throws IOException, TwitterException {
		initialize();
		//stream();
		//search("Trumph");
		getFollowersIDs("realDonaldTrump");
	}
	
	
	public static void initialize() {
		CONFIGURATION_BUILDER = new ConfigurationBuilder();
		CONFIGURATION_BUILDER.setDebugEnabled(true)
		.setOAuthConsumerKey(OAUTH_CONSUMER_KEY)
		.setOAuthConsumerSecret(OAUTH_CONSUMER_SECRET)
		.setOAuthAccessToken(OAUTH_ACCESS_TOKEN)
		.setOAuthAccessTokenSecret(OAUTH_ACCESS_TOKEN_SECRET)
		.setJSONStoreEnabled(true);
	}
	public static void shutdown() {
		if(TWITTER_STREAM != null) {
			TWITTER_STREAM.cleanUp();
			TWITTER_STREAM.shutdown();
		}		
	}
	public static void stream() throws IOException {
		TWITTER_STREAM = new TwitterStreamFactory(CONFIGURATION_BUILDER.build()).getInstance();
		
		StatusListener listener = new StatusListener() {
			public void onStatus(Status status) {
				String json = TwitterObjectFactory.getRawJSON(status);
				System.out.println(json);
			}

			public void onDeletionNotice(
					StatusDeletionNotice statusDeletionNotice) {
				// System.out.println("Got a status deletion notice id:" +
				// statusDeletionNotice.getStatusId());
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				// System.out.println("Got track limitation notice:" +
				// numberOfLimitedStatuses);

			}

			public void onScrubGeo(long userId, long upToStatusId) {
				// System.out.println("Got scrub_geo event userId:" + userId +
				// " upToStatusId:" + upToStatusId);
			}

			public void onStallWarning(StallWarning warning) {
				// System.out.println("Got stall warning:" + warning);
			}

			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};
		TWITTER_STREAM.addListener(listener);
		TWITTER_STREAM.sample();
	}
	
	public static void search(String inputQuery) throws TwitterException {
		TWITTER_FACTORY = new TwitterFactory(CONFIGURATION_BUILDER.build());
		Twitter twitter = TWITTER_FACTORY.getInstance();

		Query query = new Query(inputQuery);
		query.setCount(100);

		QueryResult results = twitter.search(query);

		int i = 0;

		for (Status status : results.getTweets()) {
			System.out.println(i++ + " : " + status.toString());
		}
		System.out.println(results.getRateLimitStatus().toString());
	}
	
	public static void getFollowersIDs(String userName) {
		try {
			Twitter twitter = new TwitterFactory(CONFIGURATION_BUILDER.build()).getInstance();
			long cursor = -1;
			IDs ids;
			System.out.println("Listing followers's ids.");
			do {
				if (userName != null) {
					ids = twitter.getFollowersIDs(userName, cursor);
				} else {
					ids = twitter.getFollowersIDs(cursor);
				}
				for (long id : ids.getIDs()) {
					System.out.println(id);
				}
			} while ((cursor = ids.getNextCursor()) != 0);

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get followers' ids: " + te.getMessage());
			System.exit(-1);
		}
	}

}
