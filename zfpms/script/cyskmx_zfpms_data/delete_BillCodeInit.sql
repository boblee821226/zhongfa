DELETE FROM pub_bcr_candiattr WHERE pk_nbcr = '0001ZZ1000000002D64J';
DELETE FROM pub_bcr_elem WHERE pk_billcodebase in ( select pk_billcodebase from pub_bcr_RuleBase where nbcrcode = 'ZF06' );
DELETE FROM pub_bcr_RuleBase WHERE nbcrcode = 'ZF06';
DELETE FROM pub_bcr_nbcr WHERE pk_nbcr = '0001ZZ1000000002D64J';
DELETE FROM pub_bcr_OrgRela WHERE pk_billcodebase = '0001ZZ1000000002D64K';
DELETE FROM pub_bcr_RuleBase WHERE pk_billcodebase = '0001ZZ1000000002D64K';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ1000000002D64L';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ1000000002D64M';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ1000000002D64N';
