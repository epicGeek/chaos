# 基于DHSS三期框架的BOSS业务监控开发文档

## 说明

* 避免一切update操作。因为现在批量的update的操作效率没有测试，而且update操作必须严格要求SOAP日志先入库，才能保证数据的准确性。
* 和二期一样，SOAP日志和ERR日志应该入到两份索引里面，SOAP日志里面既有成功数据又有失败数据，ERR里面只有失败数据，成功或者失败的状态是根据是否能在ERR日志里查询到TASK_ID决定的。换句话说，查询一条数据的失败/成功状态，拿它的TASK_ID在ERR日志里面查询，如果查询的到，说明是失败的日志，如果查不到，说明是成功的。（联通版的数据task_id不是UUID，而是六位数字组成的数字ID，随着数据量变大可能，TASK_ID在SOAP日志里有可能重复。）
* 涉及到JSON和XML数据解析，推荐使用JACKSON和DOM4J

## 问题：
* 全文存储： 在页面上，针对每条数据，点击查询详情按钮，可以查看这条日志的全文（原始数据）。二期的做法是，做冗余表"boss_join",字段"task_id","response_time","soap_log","err_log"。

## 移动版：
### 数据
#### SOAP日志
```
2017-08-30 18:16:24 479|User: boss1| id:a-ce9d7a1d-d6e6-46e7-9ee0-6bd8f2897d19#1504088184479 |{"HLRSN":"64","ISDN":"8618857498406","IMSI":"460006840854710","IMPU":"tel:+8615257884705","SIFCID":"907","PRIO":"0","HLRID":"BE36","operationName":"ADD_SIFC","FTP_NEID":"","LDAP_NEID":"LDAP36","FTN_INDEX":"64","OPERATION":"ADD_SIFC_SPML"}|
```
SOAP日志字段:

| 字段          | 原始报文                      | 解析为                     |
| ------------- |:----------------------------:| -------------------------:|
| _id           | N/A                          | ES自动生成 UUID            |
| task_id       |id:a-ce9d7a1d-d6e6-...        | a-ce9d7a1d-d6e6-...        |
| response_time | 2017-08-30 18:16:24 479      |   2017-08-30 18:16:24,479  |
| user_name          | User: boss1                  |    boss1                   |
| msisdn        | "ISDN":"8618857498406"       |   "8618857498406"         |
| imsi          |  "IMSI":"460006840854710"    | "460006840854710"         |
| impu          | "IMPU":"tel:+8615257884705"  |    "tel:+8615257884705"    |
| hlrsn         | "HLRSN":"64"                 | 64(*某些地区需要转换)       |
| operation_name| "operationName":"ADD_SIFC"   | ADD_SIFC                  |
| business_type | N/A                          | 根据operation_name从表中对应，ADD_SIFC对应为VoLTE|

Operation_name与business_type的关系为多对一,对应关系代码如下(包括联通和移动的对应关系，其中四个字母以Z开头的是联通的):
* UNKNOWN或者空字符串的业务类型是在之前一期BOSS业务监控没给出业务类型，后来发现的，暂时写为UNKNOWN,如果知道了业务类型要改正。
```
static {
		BUSINESS_TYPE_MAP.put("ZVFS","UNKNOWN");
		BUSINESS_TYPE_MAP.put("ZMIS","UNKNOWN");
		BUSINESS_TYPE_MAP.put("ZMNE","LTE");
		BUSINESS_TYPE_MAP.put("ZMIM","CARD");
		BUSINESS_TYPE_MAP.put("ZMNB","STOPRESET");
		BUSINESS_TYPE_MAP.put("ZMNP","LTE");
		BUSINESS_TYPE_MAP.put("ZMNA","LTE");
		BUSINESS_TYPE_MAP.put("ZMGC","STOPRESET");
		BUSINESS_TYPE_MAP.put("ZMBC","VOICE");
		BUSINESS_TYPE_MAP.put("ZMNM","STOPRESET");
		BUSINESS_TYPE_MAP.put("ZMAE","OVERHEAD");
		BUSINESS_TYPE_MAP.put("ZMIR","OVERHEAD");
		BUSINESS_TYPE_MAP.put("ZMSD","VOICE");
		BUSINESS_TYPE_MAP.put("ZMBD","VOICE");
		BUSINESS_TYPE_MAP.put("ZMNC","GPRS");
		BUSINESS_TYPE_MAP.put("ZMIO","BQUERY");
		BUSINESS_TYPE_MAP.put("ZMSO","BQUERY");
		BUSINESS_TYPE_MAP.put("ZMNO","BQUERY");
		BUSINESS_TYPE_MAP.put("ZMQO","BQUERY");
		BUSINESS_TYPE_MAP.put("ZMAO","BQUERY");
		BUSINESS_TYPE_MAP.put("ZMNF","BQUERY");
		BUSINESS_TYPE_MAP.put("ZMSC","VOICE");
		BUSINESS_TYPE_MAP.put("ZMNI","BQUERY");
		BUSINESS_TYPE_MAP.put("ZMSS","VOICE");
		BUSINESS_TYPE_MAP.put("ZMGO","BQUERY");
		BUSINESS_TYPE_MAP.put("ZMND","GPRS");
		BUSINESS_TYPE_MAP.put("ZMQD","NETWORK");
		BUSINESS_TYPE_MAP.put("ZMQE","NETWORK");
		BUSINESS_TYPE_MAP.put("ZMID","OVERHEAD");
		BUSINESS_TYPE_MAP.put("ZMAD","OVERHEAD");
		BUSINESS_TYPE_MAP.put("ZMIO","BQUERY");
		BUSINESS_TYPE_MAP.put("ZMBO","BQUERY");
		BUSINESS_TYPE_MAP.put("ZMNR","LTE");
		BUSINESS_TYPE_MAP.put("ZMND","GPRS");
		BUSINESS_TYPE_MAP.put("ZVIR","voLTE");
		BUSINESS_TYPE_MAP.put("ZVID","voLTE");
		BUSINESS_TYPE_MAP.put("ADD_KI","OVERHEAD");
		BUSINESS_TYPE_MAP.put("RMV_KI","OVERHEAD");
		BUSINESS_TYPE_MAP.put("MOD_ARD","GPRS_LTE");
		BUSINESS_TYPE_MAP.put("LST_ARD","");
		BUSINESS_TYPE_MAP.put("MOD_BS","VOICE");
		BUSINESS_TYPE_MAP.put("LST_BS","");
		BUSINESS_TYPE_MAP.put("MOD_CFU","VOICE");
		BUSINESS_TYPE_MAP.put("MOD_CFNRC","VOICE");
		BUSINESS_TYPE_MAP.put("REG_CFNRC","VOICE");
		BUSINESS_TYPE_MAP.put("ERA_CFNRC","VOICE");
		BUSINESS_TYPE_MAP.put("MOD_CFD","VOICE");
		BUSINESS_TYPE_MAP.put("LST_CFALL","");
		BUSINESS_TYPE_MAP.put("REG_CFU","VOICE");
		BUSINESS_TYPE_MAP.put("ERA_CFU","VOICE");
		BUSINESS_TYPE_MAP.put("MOD_CFB","VOICE");
		BUSINESS_TYPE_MAP.put("REG_CFB","VOICE");
		BUSINESS_TYPE_MAP.put("ERA_CFB","VOICE");
		BUSINESS_TYPE_MAP.put("MOD_CFNRY","VOICE");
		BUSINESS_TYPE_MAP.put("REG_CFNRY","VOICE");
		BUSINESS_TYPE_MAP.put("ERA_CFNRY","VOICE");
		BUSINESS_TYPE_MAP.put("MOD_CB","VOICE");
		BUSINESS_TYPE_MAP.put("ACT_BICROM","VOICE");
		BUSINESS_TYPE_MAP.put("DEA_BICROM","VOICE");
		BUSINESS_TYPE_MAP.put("LST_CBAR","");
		BUSINESS_TYPE_MAP.put("MOD_BARPWD","VOICE");
		BUSINESS_TYPE_MAP.put("MOD_CBCOU","VOICE");
		BUSINESS_TYPE_MAP.put("ACT_BAOC","VOICE");
		BUSINESS_TYPE_MAP.put("DEA_BAOC","VOICE");
		BUSINESS_TYPE_MAP.put("ACT_BOIC","VOICE");
		BUSINESS_TYPE_MAP.put("DEA_BOIC","VOICE");
		BUSINESS_TYPE_MAP.put("ACT_BOICEXHC","VOICE");
		BUSINESS_TYPE_MAP.put("DEA_BOICEXHC","VOICE");
		BUSINESS_TYPE_MAP.put("ACT_BAIC","VOICE");
		BUSINESS_TYPE_MAP.put("DEA_BAIC","VOICE");
		BUSINESS_TYPE_MAP.put("MOD_CLIP","IDENTIFICATION");
		BUSINESS_TYPE_MAP.put("LST_CLIP","");
		BUSINESS_TYPE_MAP.put("MOD_CLIR","IDENTIFICATION");
		BUSINESS_TYPE_MAP.put("LST_CLIR","");
		BUSINESS_TYPE_MAP.put("MOD_COLP","IDENTIFICATION");
		BUSINESS_TYPE_MAP.put("LST_COLP","");
		BUSINESS_TYPE_MAP.put("MOD_COLR","IDENTIFICATION");
		BUSINESS_TYPE_MAP.put("LST_COLR","");
		BUSINESS_TYPE_MAP.put("MOD_PLMNSS","CUSTOM");
		BUSINESS_TYPE_MAP.put("MOD_OSS","VOICE");
		BUSINESS_TYPE_MAP.put("LST_OSS","");
		BUSINESS_TYPE_MAP.put("LST_SS","");
		BUSINESS_TYPE_MAP.put("MOD_LCS","LOCATION");
		BUSINESS_TYPE_MAP.put("LST_LCS","");
		BUSINESS_TYPE_MAP.put("MOD_CARP","VOICE");
		BUSINESS_TYPE_MAP.put("LST_CARP","");
		BUSINESS_TYPE_MAP.put("ADD_SUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("MOD_IMSI","OVERHEAD");
		BUSINESS_TYPE_MAP.put("MOD_ISDN","OVERHEAD");
		BUSINESS_TYPE_MAP.put("MOD_CATEGORY","USERTYPE");
		BUSINESS_TYPE_MAP.put("LST_CATEGORY","");
		BUSINESS_TYPE_MAP.put("MOD_NAM","GPRS_VOICE");
		BUSINESS_TYPE_MAP.put("LST_NAM","");
		BUSINESS_TYPE_MAP.put("MOD_CCGLOBAL","CHARGING");
		BUSINESS_TYPE_MAP.put("LST_CCGLOBAL","");
		BUSINESS_TYPE_MAP.put("ADD_TPLSUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("RMV_SUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("ADD_CSPSSUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("ADD_TPLCSPSSUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("RMV_CSPSSUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("ADD_EPSSUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("RMV_EPSSUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("LST_SUB","");
		BUSINESS_TYPE_MAP.put("MOD_CAMEL","NETWORK");
		BUSINESS_TYPE_MAP.put("LST_CAMEL","");
		BUSINESS_TYPE_MAP.put("SND_CANCELC","LOCATION");
		BUSINESS_TYPE_MAP.put("MOD_TS","VOICE");
		BUSINESS_TYPE_MAP.put("LST_TS","");
		BUSINESS_TYPE_MAP.put("MOD_TPLGPRS","GPRS");
		BUSINESS_TYPE_MAP.put("MOD_GPRS_CONTEXT","GPRS");
		BUSINESS_TYPE_MAP.put("LST_GPRS","");
		BUSINESS_TYPE_MAP.put("MOD_TPLEPS","LTE");
		BUSINESS_TYPE_MAP.put("MOD_EPSDATA","LTE");
		BUSINESS_TYPE_MAP.put("MOD_EPS_CONTEXT","LTE");
		BUSINESS_TYPE_MAP.put("LST_EPS","");
		BUSINESS_TYPE_MAP.put("MOD_DIAMRRS","LTE");
		BUSINESS_TYPE_MAP.put("LST_DIAMRRS","");
		BUSINESS_TYPE_MAP.put("ADD_TPLIMSSUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("MOD_TPLIMSSUB","VoLTE");
		BUSINESS_TYPE_MAP.put("ADD_IMSSUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("MOD_CAP","VoLTE");
		BUSINESS_TYPE_MAP.put("LST_STNSR","");
		BUSINESS_TYPE_MAP.put("ADD_SIFC","VoLTE");
		BUSINESS_TYPE_MAP.put("RMV_SIFC","VoLTE");
		BUSINESS_TYPE_MAP.put("LST_SIFC","");
		BUSINESS_TYPE_MAP.put("ADD_IFC","VoLTE");
		BUSINESS_TYPE_MAP.put("RMV_IFC","VoLTE");
		BUSINESS_TYPE_MAP.put("LST_IFC","");
		BUSINESS_TYPE_MAP.put("MOD_VOLTETAG","VoLTE");
		BUSINESS_TYPE_MAP.put("LST_VOLTETAG","");
		BUSINESS_TYPE_MAP.put("MOD_HBAR","VoLTE");
		BUSINESS_TYPE_MAP.put("LST_CAP","");
		BUSINESS_TYPE_MAP.put("LST_HBAR","");
		BUSINESS_TYPE_MAP.put("MOD_CHARGID","VoLTE");
		BUSINESS_TYPE_MAP.put("LST_CHARGID","");
		BUSINESS_TYPE_MAP.put("MOD_VNTPLID","VoLTE");
		BUSINESS_TYPE_MAP.put("LST_VNTPLID","");
		BUSINESS_TYPE_MAP.put("MOD_MEDIAID","VoLTE");
		BUSINESS_TYPE_MAP.put("LST_MEDIAID","");
		BUSINESS_TYPE_MAP.put("MOD_STNSR","VoLTE");
		BUSINESS_TYPE_MAP.put("LST_IMSSUB","");
		BUSINESS_TYPE_MAP.put("RMV_IMSSUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("MOD_LCK","STOPRESET");
		BUSINESS_TYPE_MAP.put("LST_LCK","");
		BUSINESS_TYPE_MAP.put("MOD_RR","ROAMING");
		BUSINESS_TYPE_MAP.put("LST_RR","");
		BUSINESS_TYPE_MAP.put("MOD_ODB","VOICE");
		BUSINESS_TYPE_MAP.put("LST_ODBDAT","");
		BUSINESS_TYPE_MAP.put("BAT_ADD_TPLSUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("BAT_RMV_SUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("BAT_ADD_TPLCSPSSUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("BAT_RMV_CSPSSUB","OVERHEAD");
		BUSINESS_TYPE_MAP.put("BAT_RMV_EPSDATA","OVERHEAD");
		BUSINESS_TYPE_MAP.put("BAT_ADD_KI","OVERHEAD");
		BUSINESS_TYPE_MAP.put("BAT_RMV_KI","OVERHEAD");
		BUSINESS_TYPE_MAP.put("BAT_MOD_LCK","STOPRESET");
		BUSINESS_TYPE_MAP.put("MOD_LCADDRESS","GPRS_VOICE");
	}
```

#### ERR日志
HeartBeat日志：
```
2017-08-31 10:40:12,397 : task id : a-7e7feee7-fcc0-4497-9fb6-b8a39336c47f#HeartBeat#1504147212378
=========================================================
request: 
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"><soapenv:Header><hss:UserName xmlns:hss="http://www.chinamobile.com/HSS/">boss1</hss:UserName><hss:PassWord xmlns:hss="http://www.chinamobile.com/HSS/">565630f530a957ebdcb04967b625546973b54510</hss:PassWord></soapenv:Header><soapenv:Body><hss:HeartBeat xmlns:hss="http://www.chinamobile.com/HSS/"><hss:HLRSN>60</hss:HLRSN><hss:IMSI>262023333669999</hss:IMSI></hss:HeartBeat></soapenv:Body></soapenv:Envelope>
---------------------------------------------------------
response: 
<SOAP-ENV:Envelope  xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
<SOAP-ENV:Body> 
    <HeartBeatResponse xmlns="http://www.chinamobile.com/HSS/">
        <Result> 
            <ResultCode>[3001, 3048]</ResultCode>
            <ResultDesc>[Subscriber not defined, KI not loaded]</ResultDesc>
        </Result>
    </HeartBeatResponse>
</SOAP-ENV:Body>
</SOAP-ENV:Envelope>
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
```

* HeartBeat日志根据经验看是不需要解析的，因为它没有业务类型，没法计算统计数据（失败率），并且TASK_ID在SOAP日志中不存在。HeartBeat日志实际代表什么暂且不知，现在没有解析入库也没有反馈，联通版没有HeartBeat的数据。

一般ERR日志：
```
2017-08-31 10:40:16,310 : task id : a-c1224c57-2309-4889-a4bb-779c77bfbd00#1504147216280
=========================================================
request: 
<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:hss="http://www.chinamobile.com/HSS/">
   <soapenv:Header>
      <hss:PassWord>565630f530a957ebdcb04967b625546973b54510</hss:PassWord>
      <hss:UserName>boss1</hss:UserName>
   </soapenv:Header>
   <soapenv:Body>
      <hss:MOD_EPS_CONTEXT>
         <hss:HLRSN>64</hss:HLRSN>
          <hss:ISDN>8613586891764</hss:ISDN>
         <hss:PROV>ADDPDNCNTX</hss:PROV>
         <hss:CNTXID>3</hss:CNTXID>
         <hss:EPSAPNQOSTPLID>551</hss:EPSAPNQOSTPLID>
         <hss:APN>CMNET</hss:APN>
      </hss:MOD_EPS_CONTEXT>
   </soapenv:Body>
</soapenv:Envelope>
---------------------------------------------------------
response: 
<SOAP-ENV:Envelope  xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
<SOAP-ENV:Body> 
    <MOD_EPS_CONTEXTResponse xmlns="http://www.chinamobile.com/HSS/">
        <Result> 
            <ResultCode>3006</ResultCode>
            <ResultDesc>apn:CMNET in epsPdnContext already exists.</ResultDesc>
        </Result>
    </MOD_EPS_CONTEXTResponse>
</SOAP-ENV:Body>
</SOAP-ENV:Envelope>
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
```

* ERR日志由三部分构成：1.日志信息：包括日期，task_id。 2.request : user,password,hlrsn,number(MSISDN,IMSI)。 3.response:状态码和错误描述。

ERR日志字段:

| 字段          | 原始报文                      | 解析为                     |
| ------------- |:----------------------------:| -------------------------:|
| _id           | N/A                          | ES自动生成 UUID            |
| task_id       |task id :a-c1224c57-2....     | a-c1224c57-2....          |
| response_time | 2017-08-31 10:40:16,310      |   2017-08-31 10:40:16,310  |
| user          | < hss:UserName> boss1         |    boss1                   |
| msisdn        | < hss:ISDN>8613586891764      |   "8613586891764"          |
| imsi          | < hss:IMSI>262023333669999    |   262023333669999          |
| impu          | < hss:IMPU>tel:+8615157266661 |  tel:+8615157266661        |
| hlrsn         |< hss:HLRSN>64                 | 64(*某些地区需要转换)       |
| operation_name| < hss:MOD_EPS_CONTEXT>        | MOD_EPS_CONTEXT            |
| business_type | N/A                          | 根据operation_name从表中对应为LTE|
| error_code    | <ResultCode>3006< /ResultCode>| 3006                      |
| error_message | <ResultDesc>apn:CMNET in ...< /ResultDesc>| apn:CMNET in ...|
* 一般来说，每条数据里imsi,msisdn,impu只能存在一种或两种。这是经过观察得出的结论。