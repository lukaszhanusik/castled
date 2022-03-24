---
sidebar_position: 12
---

# Mailchimp

Mailchimp is a popular email marketing tool.

## Creating an app connection

Castled establishes connection to your mailchimp destination via OAuth. Select Mailchimp as the source type, enter the OAuth ClientID and ClientSecret of the connected app. You will be redirected to the Mailchimp login screen. Login via Mailchimp to create an app connection in Castled. 

Follow the steps [here](https://mailchimp.com/developer/marketing/guides/access-user-data-oauth-2/) to register an app in mailchimp and accesss the oauth ClientID and Client Secret.

## Creating a sync pipeline

Create a segment of users you want to send customized emails to and keep them in sync with the mailchimp audience using Castled.

### App Sync Config

Select the target audience to sync the data in App Sync Settings page.

### Mailchimp Audience Fields Mapping

Select the **email** column from the source query fields to match source record to mailchimp audience. Map other user information fields like first name, last name, date of birth, address etc to corresponding mailchimp audience fields.
