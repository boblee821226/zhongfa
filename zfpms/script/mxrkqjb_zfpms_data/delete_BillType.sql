DELETE FROM bd_billtype2 WHERE pk_billtypeid = '0001ZZ100000000224LE';
DELETE FROM bd_fwdbilltype WHERE pk_billtypeid = '0001ZZ100000000224LE';
DELETE FROM pub_function WHERE pk_billtype = 'ZF05';
DELETE FROM pub_billaction WHERE pk_billtypeid = '0001ZZ100000000224LE';
DELETE FROM pub_billactiongroup WHERE pk_billtype = 'ZF05';
DELETE FROM bd_billtype WHERE pk_billtypeid = '0001ZZ100000000224LE';
delete from temppkts;
DELETE FROM sm_rule_type WHERE pk_rule_type = null;
DELETE FROM sm_permission_res WHERE pk_permission_res = null;
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000224LF';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000224LG';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000224LH';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000224LI';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000224LJ';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000224LK';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000224LL';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000224LM';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000224LN';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000224LO';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000224LP';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000224LQ';
