<%@page import="java.text.DateFormat"%>
<%@page import="com.tinhnv.model.donate.Donate"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Currency"%>
<%@page import="org.apache.tomcat.jni.Local"%>
<%@page import="java.util.Calendar"%>
<%@page import="org.apache.tomcat.util.codec.binary.Base64"%>
<%@page import="com.tinhnv.model.others.ImgEvent"%>
<%@page import="java.util.List"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.sql.Date"%>
<%@page import="com.tinhnv.model.event.EventFullInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
EventFullInfo donation = (EventFullInfo) request.getAttribute("donation");
int id = donation.getId();
String title = donation.getTitle();
String detail = donation.getDetail();
Date startDate = donation.getStartDate();
Date endDate = donation.getEndDate();
BigDecimal purpose = donation.getPurpose();
BigDecimal achievement = donation.getAchievement();
boolean status = donation.getStatus();
List<ImgEvent> images = donation.getImages();
long donationCount = donation.getDonationCount();
%>
<%!String sourceOfImage(ImgEvent image) {
		String type = image.getType();
		byte[] arr = image.getArray();
		String encoderFile = Base64.encodeBase64String(arr);
		return "<br><img src='data:" + type + ";base64," + encoderFile + "' style='width:100%;'/><br>";
	}%>
<!-- Begin Page Content -->
<style>
	figure.image>img , p>img {
		width: 100% !important;
	}
</style>
<div class="container-fluid">

	<div class="row">

		<div class="col-lg-8">

			<div class="card shadow mb-4">
				<div class="card-header py-3 d-flex justify-content-between align-items-center">
					<h6 class="m-0 font-weight-bold text-primary"><%=title%></h6>
					<div class="m-0 p-0">
						<a class="m-2 btn btn-outline-info pr-2"
							href='${pageContext.request.contextPath}/chuong-trinh/cap-nhat-chuong-trinh?eventId=<%= id %>'
							title="Ch???nh s???a th??ng tin"> <i class="fa fa-edit"
							aria-hidden="true"></i>
						</a>
					</div>
				</div>
				<div class="card-body">
					<!-- title detail and images -->
					<%
						out.println(detail);
					%>
				</div>
			</div>
		</div>

		<!-- Second Column -->
		<div class="col-lg-4">

			<!-- Background Gradient Utilities -->
			<div class="card shadow mb-4">
				<div class="card-header py-3">
					<h6 class="m-0 font-weight-bold text-primary">Ti???n ?????</h6>
				</div>
				<div class="card-body">
					<div class="no-gutters align-items-center">
						<div>
							<div
								class="text-xs font-weight-bold text-success text-uppercase mb-1">
								T???ng s??? l?????t quy??n g??p</div>
							<div class="h5 font-weight-bold text-gray-800">
								<!-- t???ng s??? l?????t quy??n g??p -->
								<%=donationCount%>
							</div>
						</div>
					</div>
					<div class="no-gutters align-items-center">
						<div>
							<div
								class="text-xs font-weight-bold text-info text-uppercase mb-1">
								???? quy??n g??p ???????c</div>
							<div class="no-gutters align-items-center">
								<div>
									<p class="font-weight-bold text-gray-800">
										<!-- s??? ti???n quy??n g??p ???????c ?????n th???i ??i???m hi???n t???i -->
										<%
										Locale locale = new Locale("vi", "VN");
										achievement = achievement.setScale(0);
										purpose = purpose.setScale(0);
										NumberFormat format = NumberFormat.getCurrencyInstance(locale);
										out.println(format.format(achievement.doubleValue()) + " / " + format.format(purpose.doubleValue()));
										%>
									</p>
								</div>
								<div>
									<div class="progress mb-2">
										<%
										double result = achievement.doubleValue() / purpose.doubleValue() * 100;
										int percent = (int) result;
										%>
										<!-- t??nh to??n v?? ghi s??? % ??? ????y n???a-->
										<div class="progress-bar bg-info" role="progressbar"
											style="width:<%=percent%>%"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="no-gutters align-items-center">
						<div>
							<div
								class="text-xs font-weight-bold text-warning text-uppercase mb-1">
								Th???i gian c??n l???i</div>
							<div class="h5 font-weight-bold text-gray-800">
								<!-- t??nh to??n v?? vi???t s??? ng??y c??n l???i v??o ????y -->
								<%
								Calendar start = Calendar.getInstance();
								Date now = new Date(System.currentTimeMillis());
								start.setTime(now);
								Calendar end = Calendar.getInstance();
								if (endDate != null) {
									end.setTime(endDate);
									long days = (end.getTime().getTime() - start.getTime().getTime()) / (1000 * 60 * 60 * 24);
									if(days >= 0) {
										out.println(days + " ng??y");
									} else {
										out.println("???? k???t th??c");
									}
								} else {
									out.println("Ch??a ?????t gi???i h???n th???i gian.");
								}
								%>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="card shadow mb-4">
				<div class="card-header py-3 d-flex justify-content-between">
					<h6 class="m-0 font-weight-bold text-primary">Top 10 ????ng g??p</h6>
					<a href="https://love-and-care.herokuapp.com/admin/dong-thoi-gian?eventId=<%= id %>" class="btn btn-sm btn-outline-success">Xem nhi???u h??n</a>
				</div>
				<div class="card-body">
					<table class="table table-hover">
						<thead>
							<tr>
								<th class="text-nowrap">T??n</th>
								<th class="text-nowrap">????ng g??p</th>
								<th class="text-nowrap">Ng??y</th>
								<th class="text-nowrap">L???i nh???n</th>
							</tr>
						</thead>
						<tbody>
							<!-- b???ng danh s??ch ??? ????y DANH S??CH TOP 10 T??I KHO???N ????NG G??P NHI???U NH???T -->
							<%
							List<Donate> topDonate = donation.getTopDonate();
							for (Donate donate : topDonate) {
								String accountName = donate.getAccountName();
								BigDecimal money = donate.getMoney();
								Date date = donate.getDate();
								String message = donate.getMessage();
								boolean secret = donate.isSecret();
								out.println("<tr><td>" + (secret ? "Nh?? h???o t??m" : accountName) + "</td><td>" + format.format(money.doubleValue())
								+ "</td><td>" + DateFormat.getDateInstance().format(date) + "</td><td>" + message);
							}
							%>
						</tbody>
					</table>
				</div>
			</div>
		</div>

	</div>

</div>
<!-- /.container-fluid -->

<!-- Scroll to Top Button-->
<a class="scroll-to-top rounded" href="#page-top"> <i
	class="fas fa-angle-up"></i>
</a>