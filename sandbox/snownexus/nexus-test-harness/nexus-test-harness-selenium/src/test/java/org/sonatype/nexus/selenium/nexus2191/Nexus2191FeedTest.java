package org.sonatype.nexus.selenium.nexus2191;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.mock.SeleniumTest;
import org.sonatype.nexus.mock.pages.FeedsTab;
import org.sonatype.nexus.selenium.nexus1815.LoginTest;
import org.testng.Assert;
import org.testng.annotations.Test;

@Component( role = Nexus2191FeedTest.class )
public class Nexus2191FeedTest
    extends SeleniumTest
{

    @Test
    public void authFeed()
    {
        LoginTest.doLogin( main );

        Assert.assertTrue( main.viewsPanel().systemFeedsAvailable() );

        FeedsTab feeds = main.openFeeds().selectCategory( "authcAuthz" ).selectFeed( 0 );

        Assert.assertEquals( "Authentication", feeds.getFeedData( "title" ) );
        Assert.assertTrue( feeds.getFeedData( "description" ).startsWith(
                                                                          "Successfully authenticated user [admin] from address/host" ) );
        Assert.assertEquals( "", feeds.getFeedData( "author" ) );
        Assert.assertEquals( "", feeds.getFeedData( "content" ) );
    }

}
