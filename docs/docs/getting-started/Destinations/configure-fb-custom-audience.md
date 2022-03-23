---
sidebar_position: 4
---

# Facebook Custom Audience

A Custom Audience is an ad targeting option that lets you find your existing audiences among people who are on Facebook. Using this connector you can use an existing customer lists to create **Custom Audiences** of people who already know your business.

## Creating an app connection

![fb app config form](/img/screens/destinations/fb-audience/app_fb_custom_audience_config.png)

- **Destination App Name** - Any name for your app connection.

![fb app secret form](/img/screens/destinations/fb-audience/app_fb_config_id_and_secret.png)

- **OAuth Client id** - Your Facebook App ID.
- **OAuth Client secret** - Your Facebook App Secret.

### Creating a new facebook app

If you don't have a facebook app, you can create a new one by visiting [https://developer.facebook.com](https://developer.facebook.com).

1. Login, navigate to `My Apps` page and click on `Create`.
2. Make sure you select app type as `None` for the new app.
   ![fb app types](/img/screens/destinations/fb-audience/app_fb_types.png)
3. Also add `Facebook Login` and `Marketing API` as products to your app using the `Add Products` menu on the left sidebar.

   ![fb app product add](/img/screens/destinations/fb-audience/app_fb_product_add.png)

App will be in development mode by default and it should be good enough as long as you are working with ad accounts within your our own fb account.

## Creating a sync pipeline

![fb app config form](/img/screens/destinations/fb-audience/app_fb_custom_audience_sync_config.png)

- **Ads Account** - Select the ads account where you want to send the customer list to.
- **Custom Audience Name** - You can specify an existing audience name or a new one. If the audience name doesn't exist we create a new one by the name you specified.
- **Hashing Required** - Facebook requires all the personally identifiable user information to be hashed before syncing it. You can select **Yes** option if you want Castled to do the normalization and hashing. In case you already have the hashed information in your warehouse select **No**.

More information about facebook user fields and hashing requirements can be found [here](https://developers.facebook.com/docs/marketing-api/audiences/guides/custom-audiences/#hash)

### Customer list fields

Castled lets you sync all customer fields that facebook supports. Most of these fields are fairly straightforward and you can find their name and description [here](https://developers.facebook.com/docs/marketing-api/audiences/guides/custom-audiences/#hash).

A few fields worth mentioning:

1. `EXTERN_ID` - You can match people for an audience with your own identifiers, known as External Identifiers (EXTERN_ID). This can be any unique ID from the advertiser, such as loyalty membership IDs, user IDs, and external cookie IDs.
2. `MADID` - Mobile advertiser ID. For e.g. An advertsing ID that Google provides as part of Google Play services (Android Advertising ID)

The above fields doesn't need any hashing (If Castled is doing hashing for you, this is taken care of)
