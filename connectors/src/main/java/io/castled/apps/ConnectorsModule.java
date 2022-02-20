package io.castled.apps;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import io.castled.OptionsReferences;
import io.castled.apps.connectors.customerio.CIOEventTypeFetcher;
import io.castled.apps.connectors.customerio.CIOPrimaryKeyOptionsFetcher;
import io.castled.apps.connectors.fbcustomaudience.FbAdAccountOptionsFetcher;
import io.castled.apps.connectors.fbcustomaudience.HashingOptionsFetcher;
import io.castled.apps.connectors.googleads.*;
import io.castled.apps.connectors.intercom.*;
import io.castled.apps.connectors.sendgrid.SendgridListsOptionsFetcher;
import io.castled.apps.optionfetchers.AppOptionsFetcher;
import io.castled.commons.optionfetchers.AWSRegionOptionsFetcher;
import io.castled.commons.optionfetchers.ISO4217CurrencyCodesFetcher;
import io.castled.commons.optionfetchers.ZoneIdOptionsFetcher;
import io.castled.forms.StaticOptionsFetcher;
import io.castled.jdbc.JdbcConnectionType;
import io.castled.jdbc.JdbcQueryHelper;
import io.castled.jdbc.redshift.RedshiftQueryHelper;
import io.castled.jdbc.snowflake.SnowflakeQueryHelper;
import io.castled.optionsfetchers.appsync.AppSyncOptionsFetcher;
import io.castled.optionsfetchers.appsync.ObjectOptionsFetcher;
import io.castled.optionsfetchers.appsync.SyncModeOptionsFetcher;
import io.castled.warehouses.WarehouseConnectorConfig;
import io.castled.warehouses.connectors.bigquery.BQLocationsFetcher;
import io.castled.warehouses.connectors.postgres.PostgresQueryHelper;
import io.castled.warehouses.optionsfetchers.WarehouseOptionsFetcher;

@SuppressWarnings("rawtypes")
public class ConnectorsModule extends AbstractModule {

    private final WarehouseConnectorConfig warehouseConnectorConfig;

    public ConnectorsModule(WarehouseConnectorConfig warehouseConnectorConfig) {
        this.warehouseConnectorConfig = warehouseConnectorConfig;
    }

    protected void configure() {
        bindIntercomObjectSinks();
        bindWarehouseOptionFetchers();
        bindAppSyncOptions();
        bindJdbcQueryHelpers();
        bindStaticOptionFetchers();
        bindWarehouseOptionFetchers();
        bindAppOptionFetchers();
    }


    private void bindWarehouseOptionFetchers() {
        MapBinder<String, WarehouseOptionsFetcher> warehouseOptionFetchers = MapBinder.newMapBinder(binder(),
                String.class, WarehouseOptionsFetcher.class);
    }

    private void bindAppOptionFetchers() {
        MapBinder<String, AppOptionsFetcher> warehouseOptionFetchers = MapBinder.newMapBinder(binder(),
                String.class, AppOptionsFetcher.class);
    }

    private void bindJdbcQueryHelpers() {
        MapBinder<JdbcConnectionType, JdbcQueryHelper> queryHelpers = MapBinder.newMapBinder(binder(),
                JdbcConnectionType.class, JdbcQueryHelper.class);
        queryHelpers.addBinding(JdbcConnectionType.REDSHIFT).to(RedshiftQueryHelper.class);
        queryHelpers.addBinding(JdbcConnectionType.SNOWFLAKE).to(SnowflakeQueryHelper.class);
        queryHelpers.addBinding(JdbcConnectionType.POSTGRES).to(PostgresQueryHelper.class);
    }


    private void bindAppSyncOptions() {
        MapBinder<String, AppSyncOptionsFetcher> optionFetchers = MapBinder.newMapBinder(binder(),
                String.class, AppSyncOptionsFetcher.class);
        optionFetchers.addBinding(OptionsReferences.OBJECT).to(ObjectOptionsFetcher.class);
        optionFetchers.addBinding(OptionsReferences.SYNC_MODE).to(SyncModeOptionsFetcher.class);
        optionFetchers.addBinding(OptionsReferences.GADS_ACCOUNT_ID).to(GadAccountOptionsFetcher.class);
        optionFetchers.addBinding(OptionsReferences.GADS_LOGIN_ACCOUNT_ID).to(GadsLoginCustomerOptionsFetcher.class);
        optionFetchers.addBinding(OptionsReferences.SENDGRID_LISTS).to(SendgridListsOptionsFetcher.class);
        optionFetchers.addBinding(OptionsReferences.CIO_PRIMARY_KEYS).to(CIOPrimaryKeyOptionsFetcher.class);
        optionFetchers.addBinding(OptionsReferences.CIO_EVENT_TYPES).to(CIOEventTypeFetcher.class);
        optionFetchers.addBinding(OptionsReferences.GADS_SUB_RESOURCE).to(GoogleAdsSubResourceFetcher.class);
        optionFetchers.addBinding(OptionsReferences.FB_ADS_ACCOUNTS).to(FbAdAccountOptionsFetcher.class);
    }

    private void bindStaticOptionFetchers() {
        MapBinder<String, StaticOptionsFetcher> staticOptionFetcher = MapBinder.newMapBinder(binder(),
                String.class, StaticOptionsFetcher.class);
        staticOptionFetcher.addBinding(OptionsReferences.GCP_LOCATIONS).to(BQLocationsFetcher.class);
        staticOptionFetcher.addBinding(OptionsReferences.CUSTOMER_MATCH_TYPE).to(CustomerMatchTypeOptionsFetcher.class);
        staticOptionFetcher.addBinding(OptionsReferences.AWS_REGIONS).to(AWSRegionOptionsFetcher.class);
        staticOptionFetcher.addBinding(OptionsReferences.ZONE_IDS).to(ZoneIdOptionsFetcher.class);
        staticOptionFetcher.addBinding(OptionsReferences.CURRENCY_CODES).to(ISO4217CurrencyCodesFetcher.class);
        staticOptionFetcher.addBinding(OptionsReferences.HASHING_OPTIONS).to(HashingOptionsFetcher.class);
    }

    private void bindIntercomObjectSinks() {
        MapBinder<IntercomObject, IntercomObjectSink> pipelineDataSinks = MapBinder.newMapBinder(binder(),
                IntercomObject.class, IntercomObjectSink.class);
        pipelineDataSinks.addBinding(IntercomObject.COMPANY).to(IntercomCompanySink.class);
        pipelineDataSinks.addBinding(IntercomObject.CONTACT).to(IntercomContactSink.class);
        pipelineDataSinks.addBinding(IntercomObject.USER).to(IntercomContactSink.class);
        pipelineDataSinks.addBinding(IntercomObject.LEAD).to(IntercomContactSink.class);
    }

    @Provides
    @Singleton
    public WarehouseConnectorConfig providesWarehouseConfig() {
        return warehouseConnectorConfig;
    }
}
