DELETE FROM bd_billtype2 WHERE pk_billtypeid = '0001ZZ1000000002D646';
DELETE FROM bd_fwdbilltype WHERE pk_billtypeid = '0001ZZ1000000002D646';
DELETE FROM pub_function WHERE pk_billtype = 'ZF06';
DELETE FROM pub_billaction WHERE pk_billtypeid = '0001ZZ1000000002D646';
DELETE FROM pub_billactiongroup WHERE pk_billtype = 'ZF06';
DELETE FROM bd_billtype WHERE pk_billtypeid = '0001ZZ1000000002D646';
delete from temppkts;
DELETE FROM sm_rule_type WHERE pk_rule_type = null;
DELETE FROM sm_permission_res WHERE pk_permission_res = null;
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ1000000002D647';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ1000000002D648';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ1000000002D649';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ1000000002D64A';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ1000000002D64B';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ1000000002D64C';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ1000000002D64D';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ1000000002D64E';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ1000000002D64F';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ1000000002D64G';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ1000000002D64H';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ1000000002D64I';
