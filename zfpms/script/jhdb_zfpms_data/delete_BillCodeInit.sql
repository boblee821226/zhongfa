DELETE FROM pub_bcr_candiattr WHERE pk_nbcr = '0001ZZ100000000220NU';
DELETE FROM pub_bcr_elem WHERE pk_billcodebase in ( select pk_billcodebase from pub_bcr_RuleBase where nbcrcode = 'ZF02' );
DELETE FROM pub_bcr_RuleBase WHERE nbcrcode = 'ZF02';
DELETE FROM pub_bcr_nbcr WHERE pk_nbcr = '0001ZZ100000000220NU';
DELETE FROM pub_bcr_OrgRela WHERE pk_billcodebase = '0001ZZ100000000220NV';
DELETE FROM pub_bcr_RuleBase WHERE pk_billcodebase = '0001ZZ100000000220NV';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ100000000220NW';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ100000000220NX';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ100000000220NY';
