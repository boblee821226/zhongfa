DELETE FROM pub_bcr_candiattr WHERE pk_nbcr = '0001ZZ100000000222QX';
DELETE FROM pub_bcr_elem WHERE pk_billcodebase in ( select pk_billcodebase from pub_bcr_RuleBase where nbcrcode = 'ZF03' );
DELETE FROM pub_bcr_RuleBase WHERE nbcrcode = 'ZF03';
DELETE FROM pub_bcr_nbcr WHERE pk_nbcr = '0001ZZ100000000222QX';
DELETE FROM pub_bcr_OrgRela WHERE pk_billcodebase = '0001ZZ100000000222QY';
DELETE FROM pub_bcr_RuleBase WHERE pk_billcodebase = '0001ZZ100000000222QY';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ100000000222QZ';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ100000000222R0';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ100000000222R1';
DELETE FROM bd_billtype2 WHERE pk_billtypeid = '0001ZZ100000000222QK';
DELETE FROM bd_fwdbilltype WHERE pk_billtypeid = '0001ZZ100000000222QK';
DELETE FROM pub_function WHERE pk_billtype = 'ZF03';
DELETE FROM pub_billaction WHERE pk_billtypeid = '0001ZZ100000000222QK';
DELETE FROM pub_billactiongroup WHERE pk_billtype = 'ZF03';
DELETE FROM bd_billtype WHERE pk_billtypeid = '0001ZZ100000000222QK';
delete from temppkts;
DELETE FROM sm_rule_type WHERE pk_rule_type = null;
DELETE FROM sm_permission_res WHERE pk_permission_res = null;
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000222QL';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000222QM';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000222QN';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000222QO';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000222QP';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ100000000222QQ';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000222QR';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000222QS';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000222QT';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000222QU';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000222QV';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ100000000222QW';
DELETE FROM pub_systemplate_base where pk_systemplate = '0001ZZ100000000222QJ';
delete from pub_print_datasource where ctemplateid = '0001ZZ100000000220SI';
delete from pub_print_cell where ctemplateid = '0001ZZ100000000220SI';
delete from pub_print_line where ctemplateid = '0001ZZ100000000220SI';
delete from pub_print_variable where ctemplateid = '0001ZZ100000000220SI';
delete from pub_print_template where ctemplateid = '0001ZZ100000000220SI';
DELETE FROM pub_systemplate_base where pk_systemplate = '0001ZZ100000000220SH';
delete from pub_query_condition where pk_templet = '0001ZZ100000000220R3';
delete from pub_query_templet where id = '0001ZZ100000000220R3';
DELETE FROM pub_systemplate_base where pk_systemplate = '0001ZZ100000000220R2';
delete from pub_billtemplet_b where pk_billtemplet = '0001ZZ100000000220O2';
delete from pub_billtemplet where pk_billtemplet = '0001ZZ100000000220O2';
DELETE FROM pub_billtemplet_t WHERE pk_billtemplet = '0001ZZ100000000220O2';
DELETE FROM sm_menuitemreg WHERE pk_menuitem = '0001ZZ100000000220O1';
DELETE FROM sm_funcregister WHERE cfunid = '0001ZZ100000000220NZ';
DELETE FROM sm_paramregister WHERE pk_param = '0001ZZ100000000220O0';