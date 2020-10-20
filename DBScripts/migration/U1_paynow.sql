DROP TABLE `paynow_txn_data`;
ALTER TABLE `ta02_acq_mer_account` DROP KEY `POSID_ACQID_CURRENCY_IDX`;
ALTER TABLE `tm15_merchant_cfg_attr` DROP KEY `MER_ID_CFG_ATTR_NAME_IDX`;
DELETE FROM `nps_sequence` where NAME='paynow_dynamic_stan';
DELETE FROM `nps_sequence` where NAME='paynow_dynamic_seqnum';
ALTER TABLE `error_event` MODIFY `ERROR_MESSAGE` VARCHAR(100);
ALTER TABLE ttr30_aps_request MODIFY  RETRIEVAL_REF_NO VARCHAR(12);