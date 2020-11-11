/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.rwandareports.web.controller;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.*;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Controller prepares result page for Quarterly HIV Care and ART Reporting
 */
@Controller
public class RenderMCHController {
	
	@RequestMapping("/module/rwandareports/renderMCHDataSet.form")
	public String showReport(Model model, HttpSession session) throws Exception {
			String renderArg = (String) session
					.getAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT);

			ReportData data = null;
			try {
				data = (ReportData) session
						.getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);
				// start
				String savedDataSetKey = "mchDataSet";
				//String savedDataSetEncKey = "encFuture";
				//String savedDataSetNcdKey="defaultDataSetncd";

				if (savedDataSetKey.equals("mchDataSet")) {
					manipulateIMCI(savedDataSetKey, model, session);
					manipulateGBV(savedDataSetKey, model, session);
					manipulateANC(savedDataSetKey, model, session);
					manipulateIntrapartum(savedDataSetKey, model, session);
					manipulatePNC(savedDataSetKey, model, session);
					manipulateEPI(savedDataSetKey,model,session);
					manipulateGMP(savedDataSetKey,model,session);


					/*if(savedDataSetEncKey.equals("encFuture")){
						manipulateEncounters(savedDataSetEncKey, model, session);

					}*/

				}

				/*if (savedDataSetNcdKey.equals("defaultDataSetncd")){
					manipulatencdDsd(savedDataSetNcdKey, model, session);

				}*/

				// end of if

			} catch (ClassCastException ex) {
				// pass
			}
			if (data == null)
				return "redirect:../reporting/dashboard/index.form";

			@SuppressWarnings("unused")
			SimpleDataSet dataSet = (SimpleDataSet) data.getDataSets().get(
					renderArg);
			// model.addAttribute("columns", dataSet.getMetaData());
			return null;
		}

	public String manipulateIMCI(
			@RequestParam(required = false, value = "savedDataSetKey") String savedDataSetKey,
			Model model, HttpSession session) throws Exception {

		String renderArg = (String) session
				.getAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT);

		ReportData data = null;
		try {
			data = (ReportData) session
					.getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);

			savedDataSetKey=savedDataSetKey;

			List<String> savedColumnKeys = new ArrayList<String>();
			SortedMap<String,List<String>> savedColumnKeysMap=new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
			getSavedIMCIKeys(savedColumnKeysMap);
			//getSavedIMCIKeys(savedColumnKeys);
			TreeMap<String,List<DQReportModel>> imciGroupedList=new TreeMap<String, List<DQReportModel>>();

			for(Map.Entry<String, List<String>> entry: savedColumnKeysMap.entrySet()){
				List<DQReportModel> imciList = new ArrayList<DQReportModel>();

				//for(String savedColumnKey : savedColumnKeys ) {
				for(String savedColumnKey : entry.getValue() ) {
					DQReportModel dQRObject = new DQReportModel();


					for (Map.Entry<String, DataSet> e : data.getDataSets().entrySet()) {
						if (e.getKey().equals(savedDataSetKey)) {
							MapDataSet mapDataSet = (MapDataSet) e.getValue();
							DataSetColumn dataSetColumn = mapDataSet.getMetaData().getColumn(savedColumnKey);
							dQRObject.setSelectedColumn(dataSetColumn);

							Object result = mapDataSet.getData(dataSetColumn);
							Cohort selectedCohort = null;
							if (result instanceof CohortIndicatorAndDimensionResult) {
								CohortIndicatorAndDimensionResult cidr =(CohortIndicatorAndDimensionResult) mapDataSet.getData(dataSetColumn);
								selectedCohort = cidr.getCohortIndicatorAndDimensionCohort();

							}

							// Evaluate the default patient dataset definition
							DataSetDefinition dsd = null; if (dsd == null) {
								SimplePatientDataSetDefinition d = new SimplePatientDataSetDefinition();
								d.addPatientProperty("patientId"); List<PatientIdentifierType>
										types = ReportingConstants.GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES();
								if (!types.isEmpty()) {
									d.setIdentifierTypes(types);
								}

								d.addPatientProperty("givenName");
								d.addPatientProperty("familyName");
								d.addPatientProperty("age");
								d.addPatientProperty("gender");

								dsd = d;


							}

							EvaluationContext evalContext = new EvaluationContext();
							evalContext.setBaseCohort(selectedCohort);

							DataSet patientDataSet;
							try {
								patientDataSet =Context.getService( DataSetDefinitionService.class).evaluate(dsd, evalContext);
								dQRObject.setDataSet(patientDataSet);
								dQRObject.setDataSetDefinition(dsd);
							}
							catch (EvaluationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

					}

					// Add all dataset definition to the request (allow user to choose)
					dQRObject.setDataSetDefinitions(Context.getService(
							DataSetDefinitionService.class).getAllDefinitions( false));
					imciList.add(dQRObject);
				}
				imciGroupedList.put(entry.getKey(),imciList);
			}

			//model.addAttribute("imciList", imciList);
			model.addAttribute("imciGroupedList", imciGroupedList);

		}
		catch (ClassCastException ex) {
			// pass
		}
		if (data == null)
			return "redirect:../reporting/dashboard/index.form";

		@SuppressWarnings("unused")
		SimpleDataSet dataSet = (SimpleDataSet) data.getDataSets().get(
				renderArg);
		// model.addAttribute("columns", dataSet.getMetaData());
		return null;
	}


	public String manipulateGBV(
			@RequestParam(required = false, value = "savedDataSetKey") String savedDataSetKey,
			Model model, HttpSession session) throws Exception {

		String renderArg = (String) session
				.getAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT);

		ReportData data = null;
		try {
			data = (ReportData) session
					.getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);

			savedDataSetKey=savedDataSetKey;
			List<String> savedColumnKeys = new ArrayList<String>();

			getSavedGBVKeys(savedColumnKeys);
			List<DQReportModel> gbvList = new ArrayList<DQReportModel>();

			for(String savedColumnKey : savedColumnKeys ) {
				DQReportModel dQRObject = new DQReportModel();


				for (Map.Entry<String, DataSet> e : data.getDataSets().entrySet()) {
					if (e.getKey().equals(savedDataSetKey)) {
						MapDataSet mapDataSet = (MapDataSet) e.getValue();
						DataSetColumn dataSetColumn = mapDataSet.getMetaData().getColumn(savedColumnKey);
						dQRObject.setSelectedColumn(dataSetColumn);

						Object result = mapDataSet.getData(dataSetColumn);
						Cohort selectedCohort = null;
						if (result instanceof CohortIndicatorAndDimensionResult) {
							CohortIndicatorAndDimensionResult cidr =(CohortIndicatorAndDimensionResult) mapDataSet.getData(dataSetColumn);
							selectedCohort = cidr.getCohortIndicatorAndDimensionCohort();

						}

						// Evaluate the default patient dataset definition
						DataSetDefinition dsd = null; if (dsd == null) {
							SimplePatientDataSetDefinition d = new SimplePatientDataSetDefinition();
							d.addPatientProperty("patientId"); List<PatientIdentifierType>
									types = ReportingConstants.GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES();
							if (!types.isEmpty()) {
								d.setIdentifierTypes(types);
							}

							d.addPatientProperty("givenName");
							d.addPatientProperty("familyName");
							d.addPatientProperty("age");
							d.addPatientProperty("gender");

							dsd = d;


						}

						EvaluationContext evalContext = new EvaluationContext();
						evalContext.setBaseCohort(selectedCohort);

						DataSet patientDataSet;
						try {
							patientDataSet =Context.getService( DataSetDefinitionService.class).evaluate(dsd, evalContext);
							dQRObject.setDataSet(patientDataSet);
							dQRObject.setDataSetDefinition(dsd);
						}
						catch (EvaluationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

				// Add all dataset definition to the request (allow user to choose)
				dQRObject.setDataSetDefinitions(Context.getService(
						DataSetDefinitionService.class).getAllDefinitions( false));
				gbvList.add(dQRObject);
			}

			model.addAttribute("ancList", gbvList);
		}
		catch (ClassCastException ex) {
			// pass
		}
		if (data == null)
			return "redirect:../reporting/dashboard/index.form";

		@SuppressWarnings("unused")
		SimpleDataSet dataSet = (SimpleDataSet) data.getDataSets().get(
				renderArg);
		// model.addAttribute("columns", dataSet.getMetaData());
		return null;
	}


	public String manipulateANC(
				@RequestParam(required = false, value = "savedDataSetKey") String savedDataSetKey,
				Model model, HttpSession session) throws Exception {

			String renderArg = (String) session
					.getAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT);

			ReportData data = null;
			try {
				data = (ReportData) session
						.getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);

				savedDataSetKey=savedDataSetKey;
				List<String> savedColumnKeys = new ArrayList<String>();

				getSavedANCKeys(savedColumnKeys);
				List<DQReportModel> ancList = new ArrayList<DQReportModel>();

				for(String savedColumnKey : savedColumnKeys ) {
					DQReportModel dQRObject = new DQReportModel();


					for (Map.Entry<String, DataSet> e : data.getDataSets().entrySet()) {
						if (e.getKey().equals(savedDataSetKey)) {
							MapDataSet mapDataSet = (MapDataSet) e.getValue();
							DataSetColumn dataSetColumn = mapDataSet.getMetaData().getColumn(savedColumnKey);
							dQRObject.setSelectedColumn(dataSetColumn);

							Object result = mapDataSet.getData(dataSetColumn);
							Cohort selectedCohort = null;
							if (result instanceof CohortIndicatorAndDimensionResult) {
								CohortIndicatorAndDimensionResult cidr =(CohortIndicatorAndDimensionResult) mapDataSet.getData(dataSetColumn);
								selectedCohort = cidr.getCohortIndicatorAndDimensionCohort();

							}

							// Evaluate the default patient dataset definition
							DataSetDefinition dsd = null; if (dsd == null) {
								SimplePatientDataSetDefinition d = new SimplePatientDataSetDefinition();
								d.addPatientProperty("patientId"); List<PatientIdentifierType>
										types = ReportingConstants.GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES();
								if (!types.isEmpty()) {
									d.setIdentifierTypes(types);
								}

									d.addPatientProperty("givenName");
									d.addPatientProperty("familyName");
									d.addPatientProperty("age");
									d.addPatientProperty("gender");

									dsd = d;


							}

							EvaluationContext evalContext = new EvaluationContext();
							evalContext.setBaseCohort(selectedCohort);

							DataSet patientDataSet;
							try {
								patientDataSet =Context.getService( DataSetDefinitionService.class).evaluate(dsd, evalContext);
								dQRObject.setDataSet(patientDataSet);
								dQRObject.setDataSetDefinition(dsd);
							}
							catch (EvaluationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

					}

					// Add all dataset definition to the request (allow user to choose)
					dQRObject.setDataSetDefinitions(Context.getService(
							DataSetDefinitionService.class).getAllDefinitions( false));
					ancList.add(dQRObject);
				}

				model.addAttribute("ancList", ancList);
			}
			catch (ClassCastException ex) {
				// pass
			}
			if (data == null)
				return "redirect:../reporting/dashboard/index.form";

			@SuppressWarnings("unused")
			SimpleDataSet dataSet = (SimpleDataSet) data.getDataSets().get(
					renderArg);
			// model.addAttribute("columns", dataSet.getMetaData());
			return null;
		}



	public String manipulateIntrapartum(
			@RequestParam(required = false, value = "savedDataSetKey") String savedDataSetKey,
			Model model, HttpSession session) throws Exception {

		String renderArg = (String) session
				.getAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT);

		ReportData data = null;
		try {
			data = (ReportData) session
					.getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);

			savedDataSetKey=savedDataSetKey;
			List<String> savedColumnKeys = new ArrayList<String>();

			getSavedKeysIntrapartum(savedColumnKeys);
			List<DQReportModel> IntraList = new ArrayList<DQReportModel>();

			for(String savedColumnKey : savedColumnKeys ) {
				DQReportModel dQRObject = new DQReportModel();


				for (Map.Entry<String, DataSet> e : data.getDataSets().entrySet()) {
					if (e.getKey().equals(savedDataSetKey)) {
						MapDataSet mapDataSet = (MapDataSet) e.getValue();
						DataSetColumn dataSetColumn = mapDataSet.getMetaData().getColumn(savedColumnKey);
						dQRObject.setSelectedColumn(dataSetColumn);

						Object result = mapDataSet.getData(dataSetColumn);
						Cohort selectedCohort = null;
						if (result instanceof CohortIndicatorAndDimensionResult) {
							CohortIndicatorAndDimensionResult cidr =(CohortIndicatorAndDimensionResult) mapDataSet.getData(dataSetColumn);
							selectedCohort = cidr.getCohortIndicatorAndDimensionCohort();

						}

						// Evaluate the default patient dataset definition
						DataSetDefinition dsd = null; if (dsd == null) {
							SimplePatientDataSetDefinition d = new SimplePatientDataSetDefinition();
							d.addPatientProperty("patientId"); List<PatientIdentifierType>
									types = ReportingConstants.GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES();
							if (!types.isEmpty()) {
								d.setIdentifierTypes(types);
							}

							d.addPatientProperty("givenName");
							d.addPatientProperty("familyName");
							d.addPatientProperty("age");
							d.addPatientProperty("gender");

							dsd = d;


						}
						System.out.println("2. In Rendering Controlerrrrrrrrrrrrrrrrrrrrrrrrr");

						EvaluationContext evalContext = new EvaluationContext();
						evalContext.setBaseCohort(selectedCohort);

						DataSet patientDataSet;
						try {
							patientDataSet =Context.getService( DataSetDefinitionService.class).evaluate(dsd, evalContext);
							dQRObject.setDataSet(patientDataSet);
							dQRObject.setDataSetDefinition(dsd);
						}
						catch (EvaluationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

				// Add all dataset definition to the request (allow user to choose)
				dQRObject.setDataSetDefinitions(Context.getService(
						DataSetDefinitionService.class).getAllDefinitions( false));
				IntraList.add(dQRObject);
			}

			model.addAttribute("IntraList", IntraList);
		}
		catch (ClassCastException ex) {
			// pass
		}
		if (data == null)
			return "redirect:../reporting/dashboard/index.form";

		@SuppressWarnings("unused")
		SimpleDataSet dataSet = (SimpleDataSet) data.getDataSets().get(
				renderArg);
		// model.addAttribute("columns", dataSet.getMetaData());
		return null;
	}


	public String manipulatePNC(
			@RequestParam(required = false, value = "savedDataSetKey") String savedDataSetKey,
			Model model, HttpSession session) throws Exception {

		String renderArg = (String) session
				.getAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT);

		ReportData data = null;
		try {
			data = (ReportData) session
					.getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);

			savedDataSetKey=savedDataSetKey;
			List<String> savedColumnKeys = new ArrayList<String>();

			getSavedPNCKeys(savedColumnKeys);
			List<DQReportModel> pncList = new ArrayList<DQReportModel>();

			for(String savedColumnKey : savedColumnKeys ) {
				DQReportModel dQRObject = new DQReportModel();


				for (Map.Entry<String, DataSet> e : data.getDataSets().entrySet()) {
					if (e.getKey().equals(savedDataSetKey)) {
						MapDataSet mapDataSet = (MapDataSet) e.getValue();
						DataSetColumn dataSetColumn = mapDataSet.getMetaData().getColumn(savedColumnKey);
						dQRObject.setSelectedColumn(dataSetColumn);

						Object result = mapDataSet.getData(dataSetColumn);
						Cohort selectedCohort = null;
						if (result instanceof CohortIndicatorAndDimensionResult) {
							CohortIndicatorAndDimensionResult cidr =(CohortIndicatorAndDimensionResult) mapDataSet.getData(dataSetColumn);
							selectedCohort = cidr.getCohortIndicatorAndDimensionCohort();

						}

						// Evaluate the default patient dataset definition
						DataSetDefinition dsd = null; if (dsd == null) {
							SimplePatientDataSetDefinition d = new SimplePatientDataSetDefinition();
							d.addPatientProperty("patientId"); List<PatientIdentifierType>
									types = ReportingConstants.GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES();
							if (!types.isEmpty()) {
								d.setIdentifierTypes(types);
							}

							d.addPatientProperty("givenName");
							d.addPatientProperty("familyName");
							d.addPatientProperty("age");
							d.addPatientProperty("gender");

							dsd = d;


						}

						EvaluationContext evalContext = new EvaluationContext();
						evalContext.setBaseCohort(selectedCohort);

						DataSet patientDataSet;
						try {
							patientDataSet =Context.getService( DataSetDefinitionService.class).evaluate(dsd, evalContext);
							dQRObject.setDataSet(patientDataSet);
							dQRObject.setDataSetDefinition(dsd);
						}
						catch (EvaluationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

				// Add all dataset definition to the request (allow user to choose)
				dQRObject.setDataSetDefinitions(Context.getService(
						DataSetDefinitionService.class).getAllDefinitions( false));
				pncList.add(dQRObject);
			}

			model.addAttribute("pncList", pncList);
		}
		catch (ClassCastException ex) {
			// pass
		}
		if (data == null)
			return "redirect:../reporting/dashboard/index.form";

		@SuppressWarnings("unused")
		SimpleDataSet dataSet = (SimpleDataSet) data.getDataSets().get(
				renderArg);
		// model.addAttribute("columns", dataSet.getMetaData());
		return null;
	}



	public String manipulateEPI(
			@RequestParam(required = false, value = "savedDataSetKey") String savedDataSetKey,
			Model model, HttpSession session) throws Exception {

		String renderArg = (String) session
				.getAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT);

		ReportData data = null;
		try {
			data = (ReportData) session
					.getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);

			savedDataSetKey=savedDataSetKey;
			List<String> savedColumnKeys = new ArrayList<String>();

			getSavedEPIKeys(savedColumnKeys);
			List<DQReportModel> epiList = new ArrayList<DQReportModel>();

			for(String savedColumnKey : savedColumnKeys ) {
				DQReportModel dQRObject = new DQReportModel();


				for (Map.Entry<String, DataSet> e : data.getDataSets().entrySet()) {
					if (e.getKey().equals(savedDataSetKey)) {
						MapDataSet mapDataSet = (MapDataSet) e.getValue();
						DataSetColumn dataSetColumn = mapDataSet.getMetaData().getColumn(savedColumnKey);
						dQRObject.setSelectedColumn(dataSetColumn);

						Object result = mapDataSet.getData(dataSetColumn);
						Cohort selectedCohort = null;
						if (result instanceof CohortIndicatorAndDimensionResult) {
							CohortIndicatorAndDimensionResult cidr =(CohortIndicatorAndDimensionResult) mapDataSet.getData(dataSetColumn);
							selectedCohort = cidr.getCohortIndicatorAndDimensionCohort();

						}

						// Evaluate the default patient dataset definition
						DataSetDefinition dsd = null; if (dsd == null) {
							SimplePatientDataSetDefinition d = new SimplePatientDataSetDefinition();
							d.addPatientProperty("patientId"); List<PatientIdentifierType>
									types = ReportingConstants.GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES();
							if (!types.isEmpty()) {
								d.setIdentifierTypes(types);
							}

							d.addPatientProperty("givenName");
							d.addPatientProperty("familyName");
							d.addPatientProperty("age");
							d.addPatientProperty("gender");

							dsd = d;


						}

						EvaluationContext evalContext = new EvaluationContext();
						evalContext.setBaseCohort(selectedCohort);

						DataSet patientDataSet;
						try {
							patientDataSet =Context.getService( DataSetDefinitionService.class).evaluate(dsd, evalContext);
							dQRObject.setDataSet(patientDataSet);
							dQRObject.setDataSetDefinition(dsd);
						}
						catch (EvaluationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

				// Add all dataset definition to the request (allow user to choose)
				dQRObject.setDataSetDefinitions(Context.getService(
						DataSetDefinitionService.class).getAllDefinitions( false));
				epiList.add(dQRObject);
			}

			model.addAttribute("epiList", epiList);
		}
		catch (ClassCastException ex) {
			// pass
		}
		if (data == null)
			return "redirect:../reporting/dashboard/index.form";

		@SuppressWarnings("unused")
		SimpleDataSet dataSet = (SimpleDataSet) data.getDataSets().get(
				renderArg);
		// model.addAttribute("columns", dataSet.getMetaData());
		return null;
	}
	public String manipulateGMP(
			@RequestParam(required = false, value = "savedDataSetKey") String savedDataSetKey,
			Model model, HttpSession session) throws Exception {

		String renderArg = (String) session
				.getAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT);

		ReportData data = null;
		try {
			data = (ReportData) session
					.getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);

			savedDataSetKey=savedDataSetKey;
			List<String> savedColumnKeys = new ArrayList<String>();

			getSavedGMPKeys(savedColumnKeys);
			List<DQReportModel> gmpList = new ArrayList<DQReportModel>();

			for(String savedColumnKey : savedColumnKeys ) {
				DQReportModel dQRObject = new DQReportModel();


				for (Map.Entry<String, DataSet> e : data.getDataSets().entrySet()) {
					if (e.getKey().equals(savedDataSetKey)) {
						MapDataSet mapDataSet = (MapDataSet) e.getValue();
						DataSetColumn dataSetColumn = mapDataSet.getMetaData().getColumn(savedColumnKey);
						dQRObject.setSelectedColumn(dataSetColumn);

						Object result = mapDataSet.getData(dataSetColumn);
						Cohort selectedCohort = null;
						if (result instanceof CohortIndicatorAndDimensionResult) {
							CohortIndicatorAndDimensionResult cidr =(CohortIndicatorAndDimensionResult) mapDataSet.getData(dataSetColumn);
							selectedCohort = cidr.getCohortIndicatorAndDimensionCohort();

						}

						// Evaluate the default patient dataset definition
						DataSetDefinition dsd = null; if (dsd == null) {
							SimplePatientDataSetDefinition d = new SimplePatientDataSetDefinition();
							d.addPatientProperty("patientId"); List<PatientIdentifierType>
									types = ReportingConstants.GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES();
							if (!types.isEmpty()) {
								d.setIdentifierTypes(types);
							}

							d.addPatientProperty("givenName");
							d.addPatientProperty("familyName");
							d.addPatientProperty("age");
							d.addPatientProperty("gender");

							dsd = d;


						}

						EvaluationContext evalContext = new EvaluationContext();
						evalContext.setBaseCohort(selectedCohort);

						DataSet patientDataSet;
						try {
							patientDataSet =Context.getService( DataSetDefinitionService.class).evaluate(dsd, evalContext);
							dQRObject.setDataSet(patientDataSet);
							dQRObject.setDataSetDefinition(dsd);
						}
						catch (EvaluationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

				// Add all dataset definition to the request (allow user to choose)
				dQRObject.setDataSetDefinitions(Context.getService(
						DataSetDefinitionService.class).getAllDefinitions( false));
				gmpList.add(dQRObject);
			}

			model.addAttribute("gmpList", gmpList);
		}
		catch (ClassCastException ex) {
			// pass
		}
		if (data == null)
			return "redirect:../reporting/dashboard/index.form";

		@SuppressWarnings("unused")
		SimpleDataSet dataSet = (SimpleDataSet) data.getDataSets().get(
				renderArg);
		// model.addAttribute("columns", dataSet.getMetaData());
		return null;
	}

	private void getSavedIMCIKeys(Map<String,List<String>> savedColumnKeysMap) {
		List<String> imciAKeys=new ArrayList<String>();
		imciAKeys.add("IMCIA1");
		imciAKeys.add("IMCIA2");
		imciAKeys.add("IMCIA3");
		savedColumnKeysMap.put("A.Children received in IMCI services/ Enfants reçus dans le service PCIME",imciAKeys);

		List<String> imciBKeys=new ArrayList<String>();
		imciBKeys.add("IMCIB1");
		imciBKeys.add("IMCIB2");
		imciBKeys.add("IMCIB3");
		savedColumnKeysMap.put("B.Children who had general danger signs / Enfants consultés",imciBKeys);

		List<String> imciCKeys=new ArrayList<String>();
		imciCKeys.add("IMCIC1");
		imciCKeys.add("IMCIC2");
		imciCKeys.add("IMCIC3");
		savedColumnKeysMap.put("C.Children consulted for fever / Enfants consultés  pour la fièvre",imciCKeys);


/*

		List<String> imciDKeys=new ArrayList<String>();
		imciDKeys.add("IMCID1");
		imciDKeys.add("IMCID2");
		imciDKeys.add("IMCID3");
		savedColumnKeysMap.put("D.Children consulted for Cough or difficult breathing / Enfants consultés pour la toux ou  difficulté respiratoire",imciDKeys);
		List<String> imciEKeys=new ArrayList<String>();
		imciEKeys.add("IMCIE1");
		imciEKeys.add("IMCIE2");
		imciEKeys.add("IMCIE3");
		savedColumnKeysMap.put("E.Children consulted for Diarrhea diseases / Enfants consultés pour la Diarrhée",imciEKeys);
*/

		List<String> imciFKeys=new ArrayList<String>();
		imciFKeys.add("IMCIF1");
		imciFKeys.add("IMCIF2");
		imciFKeys.add("IMCIF3");
		savedColumnKeysMap.put("F.Children consulted for  Ear diseases/ Maladies de l’Oreille",imciFKeys);

		List<String> imciGKeys=new ArrayList<String>();
		imciGKeys.add("IMCIG1");
		imciGKeys.add("IMCIG2");
		imciGKeys.add("IMCIG3");
		savedColumnKeysMap.put("G.Children referred to Hospital / Enfants référés a l’ Hopital",imciGKeys);


		List<String> imciH2Keys=new ArrayList<String>();
		imciH2Keys.add("IMCIH21");
		imciH2Keys.add("IMCIH22");
		imciH2Keys.add("IMCIH23");
		savedColumnKeysMap.put("H.2.Diagnoses / diagnostique: Pneumonia / Pneumonie",imciH2Keys);
	}

	private void getSavedGBVKeys(List<String> savedColumnKeys) {
		savedColumnKeys.add("ANC1");
		//savedColumnKeys.add("ANC2");
		//savedColumnKeys.add("ANC3");

		//savedColumnKeys.add("ANC7");
		//savedColumnKeys.add("ANC8");
		//savedColumnKeys.add("ANC9");

	}

	private void getSavedANCKeys(List<String> savedColumnKeys) {
			savedColumnKeys.add("ANC1");
			savedColumnKeys.add("ANC2");
			savedColumnKeys.add("ANC3");
			savedColumnKeys.add("ANC4");

			savedColumnKeys.add("ANC7");
			savedColumnKeys.add("ANC8");
		    savedColumnKeys.add("ANC9");
			savedColumnKeys.add("ANC10");
			savedColumnKeys.add("ANC11");
			savedColumnKeys.add("ANC12");
			savedColumnKeys.add("ANC13");
			savedColumnKeys.add("ANC14");
			savedColumnKeys.add("ANC15");


	}

	private void getSavedKeysIntrapartum(List<String> savedColumnKeys) {
		savedColumnKeys.add("INTRA1");
		savedColumnKeys.add("INTRA2");
		savedColumnKeys.add("INTRA3");
		savedColumnKeys.add("INTRA4");
		savedColumnKeys.add("INTRA5");
		savedColumnKeys.add("INTRA6");
		savedColumnKeys.add("INTRA7");
		savedColumnKeys.add("INTRA8");
		savedColumnKeys.add("INTRA9");
		savedColumnKeys.add("INTRA10");
		savedColumnKeys.add("INTRA11");
		savedColumnKeys.add("INTRA12");
		savedColumnKeys.add("INTRA13");
		savedColumnKeys.add("INTRA14");
		savedColumnKeys.add("INTRA15");
		savedColumnKeys.add("INTRA16");
		savedColumnKeys.add("INTRA17");
		savedColumnKeys.add("INTRA18");
		savedColumnKeys.add("INTRA19");
		savedColumnKeys.add("INTRA20");
		savedColumnKeys.add("INTRA21");
		savedColumnKeys.add("INTRA22");
		savedColumnKeys.add("INTRA23");
		savedColumnKeys.add("INTRA24");
		savedColumnKeys.add("INTRA25");
		savedColumnKeys.add("INTRA26");
		savedColumnKeys.add("INTRA27");
		savedColumnKeys.add("INTRA28");
		savedColumnKeys.add("INTRA29");
		savedColumnKeys.add("INTRA30");
		savedColumnKeys.add("INTRA31");
		savedColumnKeys.add("INTRA32");
		savedColumnKeys.add("INTRA33");
		savedColumnKeys.add("INTRA34");
		savedColumnKeys.add("INTRA35");
		savedColumnKeys.add("INTRA36");
		savedColumnKeys.add("INTRA37");
		savedColumnKeys.add("INTRA38");
		savedColumnKeys.add("INTRA39");
		savedColumnKeys.add("INTRA40");
		savedColumnKeys.add("INTRA41");
		savedColumnKeys.add("INTRA42");
		savedColumnKeys.add("INTRA43");

	}

	private void getSavedPNCKeys(List<String> savedColumnKeys) {
		savedColumnKeys.add("PNC1");
		savedColumnKeys.add("PNC2");
		savedColumnKeys.add("PNC3");
		savedColumnKeys.add("PNC4");
		savedColumnKeys.add("PNC5");
		savedColumnKeys.add("PNC6");
		savedColumnKeys.add("PNC7");
		savedColumnKeys.add("PNC8");
		savedColumnKeys.add("PNC9");
		savedColumnKeys.add("PNC10");



	}
	private void getSavedEPIKeys(List<String> savedColumnKeys) {
		savedColumnKeys.add("EPI1");
		savedColumnKeys.add("EPI2");
		savedColumnKeys.add("EPI3");
		savedColumnKeys.add("EPI4");
		savedColumnKeys.add("EPI5");
		savedColumnKeys.add("EPI6");
		savedColumnKeys.add("EPI7");
		savedColumnKeys.add("EPI8");
		savedColumnKeys.add("EPI9");
		savedColumnKeys.add("EPI10");
		savedColumnKeys.add("EPI11");
		savedColumnKeys.add("EPI12");
		savedColumnKeys.add("EPI13");
		savedColumnKeys.add("EPI14");
		savedColumnKeys.add("EPI15");
		savedColumnKeys.add("EPI16");
		savedColumnKeys.add("EPI17");
		savedColumnKeys.add("EPI18");


	}
	private void getSavedGMPKeys(List<String> savedColumnKeys) {
		savedColumnKeys.add("GMP1");
		savedColumnKeys.add("GMP2");
		savedColumnKeys.add("GMP3");
		savedColumnKeys.add("GMP4");
		savedColumnKeys.add("GMP5");
		savedColumnKeys.add("GMP6");
		savedColumnKeys.add("GMP7");
		savedColumnKeys.add("GMP8");

	}

		private static Comparator<Encounter> COMPARATOR = new Comparator<Encounter>() {

			public int compare(Encounter enc1, Encounter enc2) {

				return enc2.getEncounterDatetime().compareTo(
						enc1.getEncounterDatetime());
			}

		};



	}