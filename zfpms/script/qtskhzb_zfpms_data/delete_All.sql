DELETE FROM pub_bcr_candiattr WHERE pk_nbcr = '0001ZZ100000000223AG';
DELETE FROM pub_bcr_elem WHERE pk_billcodebase in ( select pk_billcodebase from pub_bcr_RuleBase where nbcrcode = 'ZF04' );
DELETE FROM pub_bcr_RuleBase WHERE nbcrcode = 'ZF04';
DELETE FROM pub_bcr_nbcr WHERE pk_nbcr = '0001ZZ100000000223AG';
DELETE FROM pub_bcr_OrgRela WHERE pk_billcodebase = '0001ZZ100000000223AH';
DELETE FROM pub_bcr_RuleBase WHERE pk_billcodebase = '0001ZZ100000000223AH';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ100000000223AI';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ100000000223AJ';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ100000000223AK';
DELETE FROM bd_billtype2 WHERE pk_billtypeid = '0001ZZ100000000223A3';
DELETE FROM bd_fwdbilltype WHERE pk_billtypeid = '0001ZZ100000000223A3';
DELETE FROM pub_function WHERE pk_billtype = 'ZF04';
DELETE FROM pub_billaction WHERE pk_billtypeid = '0001ZZ100000000223A3';
DELETE FROM pub_billactiongroup WHERE pk_billtype = 'ZF04';
DELETE FROM bd_billtype WHERE pk_billtypeid = '0001ZZ100000000223A3';
delete from temppkts;
DELETE FROM sm_rule_type WHERE pk_rule_type = null;
DELETE FROM sm_permission_res WHERE pk_permission_res = null;
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000223A4';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000223A5';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000223A6';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000223A7';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000223A8';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000223A9';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000223AA';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000223AB';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000223AC';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000223AD';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000223AE';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000223AF';
DELETE FROM pub_systemplate_base where pk_systemplate = '0001ZZ100000000223A2';
delete from pub_print_datasource where ctemplateid = '0001ZZ100000000222VL';
delete from pub_print_cell where ctemplateid = '0001ZZ100000000222VL';
delete from pub_print_line where ctemplateid = '0001ZZ100000000222VL';
delete from pub_print_variable where ctemplateid = '0001ZZ100000000222VL';
delete from pub_print_template where ctemplateid = '0001ZZ100000000222VL';
DELETE FROM pub_systemplate_base where pk_systemplate = '0001ZZ100000000222VK';
delete from pub_query_condition where pk_templet = '0001ZZ100000000222U6';
delete from pub_query_templet where id = '0001ZZ100000000222U6';
DELETE FROM pub_systemplate_base where pk_systemplate = '0001ZZ100000000222U5';
delete from pub_billtemplet_b where pk_billtemplet = '0001ZZ100000000222R5';
delete from pub_billtemplet where pk_billtemplet = '0001ZZ100000000222R5';
DELETE FROM pub_billtemplet_t WHERE pk_billtemplet = '0001ZZ100000000222R5';
DELETE FROM sm_menuitemreg WHERE pk_menuitem = '0001ZZ100000000222R4';
DELETE FROM sm_funcregister WHERE cfunid = '0001ZZ100000000222R2';
DELETE FROM sm_paramregister WHERE pk_param = '0001ZZ100000000222R3';
