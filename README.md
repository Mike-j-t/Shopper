# Shopper
Android Shopping list app
Written using Android Studio
Utilises SQLite (7 Tables, Shops, Aisles, Products, ProductUsage, Rules, ShopList & Appvalues).

Features
Ordered shopping list (ordered by Shop/Aisle/Product).
Automated and prompted addition to the shopping list according to rules.
Free from Ads and in-app purchases.

Notes
Shops referred to as Stores (attempt to avoid cofusion between verb Shop and object Shop).

DB Structure
Shops Table - Columns :-
  _id                 integer   - unique identifier (thus possible to have same shop details)
  shopname            text      - name of the shop (store)
  shoporder           integer   - order that the shop (store) appears in the shopping list (lower #'s first)
  shopstreet          text      - address of the shop (store) (used for reference)
  shopcity            text      - city/area of the shop (store) (used for reference)
  shopstate           text      - state/county that the shop (store) is located in (used for reference)
  shopphone           text      - phone number of the shop (store) (used for reference)
  shopnotes           text      - notes about the shop (store) (used for reference)
  
Aisles Table - Columns :-
  _id                 integer   - unique identifier
  aislename           text      - name of the aisle/area (note created as default under some conditions)
  aisleorder          integer   - order of the aisle area within the shop (store) (lower #'s first)
  aisleshopref        integer  - id of the shop that owns the aisle
  
Products - Columns :-
  _id                 integer   - unqiue identifier of the product
  productname         text      - name of the product
  productaisleref     integer - NOTE NOT USED - REDUNDANT
  productuses         integer   - NOTE NOT USED - REDUNDANT
  productnotes        text     - NOTE NOT USED - REDUNDANT
  
ProductUsage - Columns :-
  productaisleref     integer - that aisle in which this product (as per productproductref) is stocked 
                                (note a product can be stocked in multiple aisles)
  productproductref   integer - reference to the product
  productcost         real    - the cost of this unique use of the product (how much it costs in this store/aisle)
  productbuycount     integer - how many times this product/aisle has been purchased
                                not implemented yet, but designed to cater for pre-emptive rule suggestion
  productfirstbuydate integer - date when first added to shopping list (again for pre-emptive rule suggestion)
  productlatestbuydate integer - date of the last addition to the shopping list
  mincost             real    - NOT USED BUT MAY BE
  orderinaisle        integer - the order of the product within the aisle and thus shop
  
Rules - Columns :-
  _id                 integer - unique rule identifier
  rulename            text    - name of the rule
  ruletype            integer - NOT USED BUT MAY BE
  rulepromptflag      integer - true/false wether the rule, when applied to the shopping list should be prompted
                                i.e. addition to the shopping list can be skipped
  ruleperiod          integer - The base period, DAYS (0), WEEKS(1), FORTNIGHTS(2), MONTHS(3), QUARTERS(4), YEARS(5)
                                for the determination of the addition of the rule to the shopping list
  rulemultipier       integer - A number that multiplies the ruleperiod e.g. if period is 0 and multiplier 5 then every 5 days.
  ruleactiveon        integer - The date/time that the next occurence will be added 
                                (when added then adjusted according to period and multiplier)
  ruleproductref      integer - The product to which this rule will apply
  ruleaisleref        integer - The aisle holding the product (i.e. product and aisle equates to unique productusage)
  rulesuses           integer - The number of uses (applies to shopping list) made of this rule (for potential use)
  mincost             real    - NOT USED BUT MAY BE
  maxcost             real    - NOT USED BUT MAY BE
  rulesnumbertoget    integer - Quantity to add to the shopping list
  
ShopList - Columns :-
  _id                 integer - Unique identifer for the shopping list entry
  slproductid         integer - _id of the product
  sldateadded         integer - Date that this entry was added
  slnumbertoget       integer - The quantity to get (Note adding the same product/aisle item increments this)
  sldone              integer - flag if all have been purchased (i.e if true then not included in shopping list)
                                (NOTE) theorectically 0 quantity is the same. However, 0 quantity alone results in
                                the entry still appearing in the shopping list but differently (can thus be recalled)
  productusageref     integer - Unused (would be same as slproductid)
  aisleref            integer - _id of the aisle. This slproductid and aislref defnies unique productusage entry
  
AppValues - Columns :-
  NOTE caters for storage of underlying values e.g. the list of periods used by rules
  _id                 integer - unique VALUE identifier
  valuename           text    - Name of the value (not need not be unique e.g. for arrays)
  valuetype           text    - The type of the value stored e.g. INTEGER, TEXT or REAL
  valueint            integer - The stored value, if it is of type INTEGER
  valuereal           real    - The stored value, if it is of type REAL
  valuetext           text    - The stored value, if it is of type TEXT
  valueincludeinsettings integer - UNUSED but potential to use in automatically genereating user settings
  valuesettingsinfo   text    - UNUSED but would be used in conjunction with valueincludeinsettings as text to display
  
  

