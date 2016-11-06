package mjt.shopper;
interface Constants {
    String LOG = "mjt.shopper";
    int HEADING_TEXT_SIZE = 25;
    int LIST_TEXT_SIZE = 20;
    int LOGTYPE_ERROR = 16;
    int LOGTYPE_WARNING = 8;
    int LOGTYPE_DEBUG = 4;
    int LOGTYPE_INFORMATIONAL = 2;

    String STANDARD_DDMMYYY_FORMAT = "dd/MM/yyyy";
    String EXTENDED__DATE_DORMAT = "EEE MMM d, yyyy";
    String COMPARE_DATE_FORMAT = "yyyyMMdd";
    String RULEPERIODS = "AutoAddPeriods";
    String DEBUGFLAG = "Debug";
    String LASTRULESUGGESTION = "LastRuleSuggestion";

    String PERIOD_DAYS = "DAYS";
    String PERIOD_DAYS_SINGULAR = "DAY";
    int PERIOD_DAYSASINT = 0;
    String PERIOD_WEEKS = "WEEKS";
    String PERIOD_WEEKS_SINGULAR = "WEEK";
    int PERIOD_WEEKSASINT = 1;
    String PERIOD_FORTNIGHTS = "FORTNIGHTS";
    String PERIOD_FORTNIGHTS_SINGULAR = "FORTNIGHT";
    int PERIOD_FORTNIGHTSASINT = 2;
    String PERIOD_MONTHS = "MONTHS";
    String PERIOD_MONTHS_SINGULAR = "MONTH";
    int PERIOD_MONTHSASINT = 3;
    String PERIOD_QUARTERS = "QUARTERS";
    String PERIOD_QUARTERS_SINGULAR = "QUARTER";
    int PERIOD_QUARTERSASINT = 4;
    String PERIOD_YEARS = "YEARS";
    String PERIOD_YEARS_SINGULAR = "YEAR";
    int PERIOD_YEARSASINT = 5;


    String STORELISTORDER_BY_STORE = " ORDER BY " + ShopperDBHelper.SHOPS_COLUMN_NAME + " ASC , " + ShopperDBHelper.SHOPS_COLUMN_CITY + " ASC ;";
    String STORELISTORDER_BY_CITY = " ORDER BY " + ShopperDBHelper.SHOPS_COLUMN_CITY + " ASC , " + ShopperDBHelper.SHOPS_COLUMN_STREET + " ASC ;";
    String STORELISTORDER_BY_ORDER = " ORDER BY " + ShopperDBHelper.SHOPS_COLUMN_ORDER + " ASC ," + ShopperDBHelper.SHOPS_COLUMN_NAME + " ASC ;";
    String STORELISTORDER_BY_STREET = " ORDER BY " + ShopperDBHelper.SHOPS_COLUMN_STREET + " ASC , " + ShopperDBHelper.SHOPS_COLUMN_CITY + " ASC , " +
            ShopperDBHelper.SHOPS_COLUMN_NAME + " ASC ;";
    String STORELIST_ORDER_BY_STATE = " ORDER BY " + ShopperDBHelper.SHOPS_COLUMN_STATE + " ASC , " + ShopperDBHelper.SHOPS_COLUMN_CITY + " ASC, " +
            ShopperDBHelper.SHOPS_COLUMN_NAME + " ASC ;";
    String STORELISTORDER_BY_PHONE = " ORDER BY " + ShopperDBHelper.SHOPS_COLUMN_PHONE + " ASC , " + ShopperDBHelper.SHOPS_COLUMN_NAME + " ASC ;";
    String STORELISTORDER_BY_NOTES = " ORDER BY " + ShopperDBHelper.SHOPS_COLUMN_NOTES + " ASC, " + ShopperDBHelper.SHOPS_COLUMN_NAME + " ASC ;";

    String AISLELISTORDER_BY_AISLE = " ORDER BY " + ShopperDBHelper.AISLES_COLUMN_NAME + " ASC, " + ShopperDBHelper.AISLES_COLUMN_ORDER + " ASC ;";
    String AISLELISTORDER_BY_ORDER = " ORDER BY " + ShopperDBHelper.AISLES_COLUMN_ORDER + " ASC, " + ShopperDBHelper.AISLES_COLUMN_NAME + " ASC ;";

    String PRODUCTLISTORDER_BY_PRODUCT = " ORDER BY " + ShopperDBHelper.PRODUCTS_COLUMN_NAME + " ASC, " + ShopperDBHelper.PRODUCTS_COLUMN_NOTES + " ASC ;";
    String PRODUCTLISTORDER_BY_NOTES = " ORDER BY " + ShopperDBHelper.PRODUCTS_COLUMN_NOTES + " ASC, " + ShopperDBHelper.PRODUCTS_COLUMN_NAME + " ASC ;";

    String PRODUCTSPERAISLELISTORDER_BY_PRODUCT = " ORDER BY " + ShopperDBHelper.PRODUCTS_COLUMN_NAME + " ASC ; ";
    String PRODUCTSPERAISLELISTORDER_BY_COST = " ORDER BY " + ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST + " ASC, " + ShopperDBHelper.PRODUCTS_COLUMN_NAME + " ASC ;";
    String PRODUCTSPERAISLELISTORDER_BY_ORDER = " ORDER BY " + ShopperDBHelper.PRODUCTUSAGE_COLUMN_ORDER + " ASC, " + ShopperDBHelper.PRODUCTS_COLUMN_NAME + " ASC ;";


    String PURCHASABLEPRODUCTSLISTORDER_BY_PRODUCT = " ORDER BY " + ShopperDBHelper.PRODUCTS_COLUMN_NAME + " ASC, " +
            ShopperDBHelper.SHOPS_COLUMN_ORDER + " ASC, " + ShopperDBHelper.AISLES_COLUMN_ORDER + " ASC ;";
    String PURCHASEABLEPRODUCTSLISTORDER_BY_STORE = " ORDER BY " + ShopperDBHelper.SHOPS_COLUMN_NAME + " ASC,  " +
            ShopperDBHelper.SHOPS_COLUMN_CITY + " ASC, " + ShopperDBHelper.SHOPS_COLUMN_STREET + " ASC ;";
    String PURCHASEABLEPRODUCTSLISTORDER_BY_CITY = " ORDER BY " + ShopperDBHelper.SHOPS_COLUMN_CITY + " ASC,  " +
            ShopperDBHelper.SHOPS_COLUMN_STREET + " ASC, " + ShopperDBHelper.SHOPS_COLUMN_NAME + " ASC ;";
    String PURCHASEABLEPRODUCTSLISTORDER_BY_STREET = " ORDER BY " + ShopperDBHelper.SHOPS_COLUMN_STREET + " ASC,  " +
            ShopperDBHelper.SHOPS_COLUMN_CITY + " ASC, " + ShopperDBHelper.SHOPS_COLUMN_NAME + " ASC ;";
    String PURCHASEABLEPRODUCTSLISTORDER_BY_AISLE = " ORDER BY " + ShopperDBHelper.AISLES_COLUMN_NAME + " ASC,  " +
            ShopperDBHelper.SHOPS_COLUMN_NAME + " ASC ;";
    String PURCHASEABLEPRODUCTSLISTORDER_BY_COST = " ORDER BY " + ShopperDBHelper.PRODUCTUSAGE_COLUMN_COST + " ASC,  " +
            ShopperDBHelper.PRODUCTS_COLUMN_NAME + " ASC, " + ShopperDBHelper.SHOPS_COLUMN_NAME + " ASC ;";

    String RULELISTORDER_BY_RULE = " ORDER BY " + ShopperDBHelper.RULES_TABLE_NAME + "." + ShopperDBHelper.RULES_COLUMN_NAME + " ASC ;";
    String RULELISTORDER_BY_DATE = " ORDER BY " + ShopperDBHelper.RULES_TABLE_NAME + "." + ShopperDBHelper.RULES_COLUMN_ACTIVEON + " ASC, " +
            ShopperDBHelper.RULES_TABLE_NAME + "." + ShopperDBHelper.RULES_COLUMN_NAME + " ASC ;";
    String RULELISTORDER_BY_PROMPT = " ORDER BY " + ShopperDBHelper.RULES_TABLE_NAME + "." + ShopperDBHelper.RULES_COLUMN_PROMPTFLAG + " ASC, " +
            ShopperDBHelper.RULES_TABLE_NAME + "." + ShopperDBHelper.RULES_COLUMN_NAME + " ASC ;";
}

