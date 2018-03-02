/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2016 E.R.P. Consultores y Asociados, C.A.               *
 * All Rights Reserved.                                                       *
 * Contributor(s): Yamel Senih www.erpya.com                                  *
 *****************************************************************************/
package org.spin.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.Util;

/**
 * Financial Management
 *
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 *      <li> FR [ 1583 ] New Definition for loan
 *		@see https://github.com/adempiere/adempiere/issues/1583
 */
public class MFMProduct extends X_FM_Product {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4496357377702190439L;

	public MFMProduct(Properties ctx, int FM_Product_ID, String trxName) {
		super(ctx, FM_Product_ID, trxName);
	}

	public MFMProduct(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	/**
	 * Get for all
	 * @param eventType
	 * @param tableId
	 * @param eventModelValidator
	 * @return
	 */
	private List<MFMFunctionalApplicability> getApplicability(String eventType, int tableId, String eventModelValidator) {
		StringBuffer whereClause = new StringBuffer(I_FM_FunctionalApplicability.COLUMNNAME_FM_ProductCategory_ID + " = ?");
		ArrayList<Object> params =  new ArrayList<Object>();
		//	
		whereClause.append(" AND ").append(I_FM_FunctionalApplicability.COLUMNNAME_EventType + " = ?");
		params.add(getFM_ProductCategory_ID());
		params.add(eventType);
		//	For Table
		if(tableId > 0) {
			whereClause.append(" AND ").append(I_FM_FunctionalApplicability.COLUMNNAME_AD_Table_ID + " = ?");
			params.add(tableId);
		}
		//	For Event Model Validator
		if(tableId > 0) {
			whereClause.append(" AND ").append(I_FM_FunctionalApplicability.COLUMNNAME_EventModelValidator + " = ?");
			params.add(eventModelValidator);
		}
		//	
		return new Query(getCtx(), I_FM_FunctionalApplicability.Table_Name, whereClause.toString(), get_TrxName())
				.setClient_ID()
				.setParameters(params)
				.setOnlyActiveRecords(true)
				.<MFMFunctionalApplicability>list();
	}
	
	/**
	 * Get Applicability without table
	 * @param eventType
	 * @return
	 */
	public List<MFMFunctionalApplicability> getApplicability(String eventType) {
		return getApplicability(eventType, 0, null);
	}

	/**
	 * Get from Table and event model validator
	 * @param tableName
	 * @param eventModelValidator
	 * @return
	 */
	public List<MFMFunctionalApplicability> getApplicability(String tableName, String eventModelValidator) {
		if(Util.isEmpty(tableName)) {
			return new ArrayList<MFMFunctionalApplicability>();
		}
		//	
		MTable table = MTable.get(getCtx(), tableName);
		return getApplicability(MFMFunctionalApplicability.EVENTTYPE_TableAction, table.getAD_Table_ID(), eventModelValidator);
	}
	
	/** Static Cache */
	private static CCache<Integer, MFMProduct> financialProductCacheIds = new CCache<Integer, MFMProduct>(Table_Name, 30);

	/**
	 * Get/Load Functional Product [CACHED]
	 * @param ctx context
	 * @param financialProductId
	 * @return activity or null
	 */
	public static MFMProduct getById(Properties ctx, int financialProductId) {
		if (financialProductId <= 0)
			return null;

		MFMProduct financialProduct = financialProductCacheIds.get(financialProductId);
		if (financialProduct != null && financialProduct.get_ID() > 0)
			return financialProduct;

		financialProduct = new Query(ctx , Table_Name , COLUMNNAME_FM_Product_ID + "=?" , null)
				.setClient_ID()
				.setParameters(financialProductId)
				.first();
		if (financialProduct != null && financialProduct.get_ID() > 0) {
			financialProductCacheIds.put(financialProduct.get_ID(), financialProduct);
		}
		return financialProduct;
	}
}
