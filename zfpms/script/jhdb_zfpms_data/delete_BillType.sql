DELETE FROM bd_billtype2 WHERE pk_billtypeid = '0001ZZ100000000220NH';
DELETE FROM bd_fwdbilltype WHERE pk_billtypeid = '0001ZZ100000000220NH';
DELETE FROM pub_function WHERE pk_billtype = 'ZF02';
DELETE FROM pub_billaction WHERE pk_billtypeid = '0001ZZ100000000220NH';
DELETE FROM pub_billactiongroup WHERE pk_billtype = 'ZF02';
DELETE FROM bd_billtype WHERE pk_billtypeid = '0001ZZ100000000220NH';
delete from temppkts;
DELETE FROM sm_rule_type WHERE pk_rule_type = null;
DELETE FROM sm_permission_res WHERE pk_permission_res = null;
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000220NI';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000220NJ';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000220NK';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000220NL';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000220NM';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000220NN';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000220NO';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000220NP';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000220NQ';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000220NR';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000220NS';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000220NT';
