<%@page language="java"%>  
<%
        String count = (String) session.getAttribute("COUNT");
        String type = (String) session.getAttribute("TYPE");
        if ((count == null) || !count.equals("4")) {
            response.sendRedirect("login.html");
        } else {
            if (type.equalsIgnoreCase("Admin")) {
%>
<BODY bgcolor=#330033 text=#00FF66><table border=0>	
        <link rel="stylesheet" href="css/css_input.css">
        <tr>
            <td background=images\Table1_dt1.JPG><img src=images\elearn.gif></td>
            <td align=left height=80 width=100% background=images\bannerBG.jpg>
                <b><font size=6 color=#ABDC18><center><i>A world beyond the classroom</i></center></font></b><br>
                <hr color=#0B114F>
                <script  src="script/imenus0.js" type="text/javascript"></script>
                <script  src="script/ocscript.js" type="text/javascript"></script>
            </td>
        </tr>
        <tr>
            <td valign=top background='images\global_sub_titlebox.gif'>
                <img src=images\del.jpg>
                <script  src='script\imenus1.js' type='text\javascript'></script>
                <script  src='script\ocscript1.js' type='text\javascript'></script>
                <img src=images\clip_image002.jpg>
            </td>

            <td background=images\Vista_Lovender_Withoutlogo_1600x1200.jpg>

                <style>
                    TD {
                        FONT-SIZE: 10pt; FONT-FAMILY: arial,helvetica
                    }
                </style>

                <br><br>
                <h1><center><u><font color=#663333 size=5>Add New Admin</font></u></center></h1>

                <form action=AdminAddProceed method=post>

                    <table align=center>

                        <tr><td>Admin id &nbsp;:</td><td><input type=text size=25 name=UserID></td></tr>

                        <tr><td>password&nbsp;:</td><td><input type=password size=25 name=password></td></tr>

                        <tr><td>Type&nbsp;:</td><td><input type=text size=25 value=Admin disabled></td></tr>

                        <tr><td>First Name&nbsp;:</td><td><input type=text size=25 name=FirstName></td></tr>

                        <tr><td>Middle Name&nbsp;:</td><td><input type=text size=25 name=MiddleName></td></tr>

                        <tr><td>Last Name&nbsp;:</td><td><input type=text size=25 name=LastName></td></tr>

                        <tr><td>Gender&nbsp;&nbsp;:</td><td><select name=gender/>
                        <option>M</option><option>F</option>		</td></tr>
                        <tr><td></td></tr>
                        <tr><td>Date Of Birth&nbsp;:</td><td>


                                <select name="DD">
                                <option value="01">01</option><option value="02">02</option><option value="03">03</option><option value="04">04</option><option value="05">05</option><option value="06">06</option><option value="07">07</option><option value="08">08</option><option value="09">09</option><option value="10">10</option><option value="11">11</option><option value="12">12</option><option value="13">13</option><option value="14">14</option><option value="15">15</option><option value="16">16</option><option value="17">17</option><option value="18">18</option><option value="19">19</option><option value="20">20</option><option value="21">21</option><option value="22">22</option><option value="23">23</option><option value="24">24</option><option value="25">25</option><option value="26">26</option><option value="27">27</option><option value="28">28</option><option value="29">29</option><option value="30">30</option><option value="31">31</option></select>


                                <select name="MM">
                                <option value="01">Jan</option><option value="02">Feb</option><option value="03">Mar</option><option value="04">Apr</option><option value="05">May</option><option value="06">Jun</option><option value="17">Jul</option><option value="18">Aug</option><option value="19">Sep</option><option value="10">Oct</option><option value="11">Nov</option><option value="12">Dec</option></select>


                                <select name="YYYY">
                                    <option value="1957">1957</option><option value="1958">1958</option><option value="1959">1959</option><option value="1960">1960</option><option value="1961">1961</option><option value="1962">1962</option><option value="1963">1963</option><option value="1964">1964</option><option value="1965">1965</option><option value="1966">1966</option><option value="1967">1967</option><option value="1968">1968</option><option value="1969">1969</option><option value="1970">1970</option><option value="1971">1971</option><option value="1972">1972</option><option value="1973">1973</option><option value="1974">1974</option><option value="1975">1975</option><option value="1976">1976</option><option value="1977">1977</option><option value="1978">1978</option><option value="1979">1979</option><option value="1980">1980</option><option value="1981">1981</option><option value="1982">1982</option><option value="1983">1983</option><option value="1984">1984</option><option value="1985">1985</option><option value="1986">1986</option><option value="1987">1987</option><option value="1988">1988</option><option value="1989">1989</option><option value="1990">1990</option><option value="1991">1991</option><option value="1992">1992</option><option value="1993">1993</option><option value="1994">1994</option><option value="1995">1995</option><option value="1996">1996</option><option value="1997">1997</option><option value="1998">1998</option><option value="1999">1999</option><option value="2000">2000</option><option value="2001">2001</option><option value="2002">2002</option><option value="2003">2003</option><option value="2004">2004</option><option value="2005">2005</option><option value="2006">2006</option><option value="2007">2007</option><option value="2008">2008</option>
                                </select>





                                <b>&nbsp;DD/MM/YYYY</b>
                        </td></tr>

                        <tr><td>E-mail&nbsp;:</td><td><input type=text name=Email></td></tr>

                        <tr><td>Address1&nbsp;:</td><td><input type=text size=27 name=Address1></td></tr>

                        <tr><td>Address2&nbsp;:</td><td><input type=text size=27 name=Address2></td></tr>

                        <tr><td>City&nbsp;:</td><td><input type=text name=City></td></tr>

                        <tr><td>State&nbsp;:</td><td><input type=text name=State></td></tr>

                        <tr><td>Zipcode&nbsp;:</td><td><input type=text name=Postcode></td></tr>

                        <tr><td>Country&nbsp;:</td><td>

                                <select name="Country">
                                    <option value="AE">Abu Dhubi</option>
                                    <option value="AF">Afghanistan</option>
                                    <option value="AE">Ajman</option>
                                    <option value="AE">Al Fujayrah</option>
                                    <option value="AL">Albania</option>
                                    <option value="DZ">Algeria</option>
                                    <option value="AS">American Samoa</option>
                                    <option value="AD">Andorra</option>
                                    <option value="AO">Angola</option>
                                    <option value="AI">Anguilla</option>
                                    <option value="AQ">Antarctica</option>
                                    <option value="AG">Antigua &amp; Barbudo</option>
                                    <option value="AG">Antigua West Indies</option>
                                    <option value="AR">Argentina</option>
                                    <option value="AM">Armenia</option>
                                    <option value="AW">Aruba</option>
                                    <option value="AU">Australia</option>
                                    <option value="AT">Austria</option>
                                    <option value="AZ">Azerbaijan</option>
                                    <option value="BS">Bahamas</option>
                                    <option value="BH">Bahrain</option>
                                    <option value="OT">Baker Island</option>
                                    <option value="BD">Bangladesh</option>
                                    <option value="BB">Barbados</option>
                                    <option value="BY">Belerus</option>
                                    <option value="BE">Belgium</option>
                                    <option value="BZ">Belize</option>
                                    <option value="BJ">Benin</option>
                                    <option value="BM">Bermuda</option>
                                    <option value="BT">Bhutan</option>
                                    <option value="BO">Bolivia</option>
                                    <option value="AN">Bonaire</option>
                                    <option value="BA">Bosnia and Herzegovina</option>
                                    <option value="BW">Botswana</option>
                                    <option value="BV">Bouvet Island</option>
                                    <option value="BR">Brazil</option>
                                    <option value="IO">British Indian Ocean </option>
                                    <option value="BN">Brunei</option>
                                    <option value="BG">Bulgaria</option>
                                    <option value="BF">Burkina Faso</option>
                                    <option value="OT">Burma</option>
                                    <option value="BI">Burundi</option>
                                    <option value="KH">Cambodia</option>
                                    <option value="CM">Cameroon</option>
                                    <option value="CA">Canada</option>
                                    <option value="CV">Cape Verde</option>
                                    <option value="KY">Cayman Islands</option>
                                    <option value="CF">Central African Republic</option>
                                    <option value="TD">Chad</option>
                                    <option value="CL">Chile</option>
                                    <option value="CN">China</option>
                                    <option value="CX">Christmas Island</option>
                                    <option value="CC">Cocos</option>
                                    <option value="CO">Colombia</option>
                                    <option value="KM">Comoros</option>
                                    <option value="CG">Congo</option>
                                    <option value="CK">Cook Islands</option>
                                    <option value="OT">Coral Sea Islands</option>
                                    <option value="CR">Costa Rica</option>
                                    <option value="CI">Cote d'Ivoire</option>
                                    <option value="HR">Croatia</option>
                                    <option value="CU">Cuba</option>
                                    <option value="AN">Curacao</option>
                                    <option value="OT">Cuyahoga</option>
                                    <option value="CY">Cyprus</option>
                                    <option value="CZ">Czech Republic</option>
                                    <option value="DK">Denmark</option>
                                    <option value="DJ">Djibouti</option>
                                    <option value="DM">Dominica</option>
                                    <option value="DO">Dominican Republic</option>
                                    <option value="AE">Dubia</option>
                                    <option value="TP">East Timor</option>
                                    <option value="EC">Ecuador</option>
                                    <option value="EG">Egypt</option>
                                    <option value="SV">El Salvador</option>
                                    <option value="GB">England</option>
                                    <option value="GQ">Equatorial Guinea</option>
                                    <option value="ER">Eritrea</option>
                                    <option value="EE">Estonia</option>
                                    <option value="ET">Ethiopia</option>
                                    <option value="FK">Falkland Islands</option>
                                    <option value="FO">Faroe Islands</option>
                                    <option value="FJ">Fiji</option>
                                    <option value="FI">Finland</option>
                                    <option value="FR">France</option>
                                    <option value="GF">French Guiana (territory)</option>
                                    <option value="PF">French Polynesia</option>
                                    <option value="TF">French Southern Territory</option>
                                    <option value="GA">Gabon</option>
                                    <option value="GM">Gambia</option>
                                    <option value="OT">Gaza Strip</option>
                                    <option value="GE">Georgia</option>
                                    <option value="DE">Germany</option>
                                    <option value="GH">Ghana</option>
                                    <option value="GI">Girbraltar</option>
                                    <option value="GB">Great Britain</option>
                                    <option value="GR">Greece</option>
                                    <option value="GL">Greenland</option>
                                    <option value="GD">Grenada</option>
                                    <option value="GP">Guadelope</option>
                                    <option value="GU">Guam</option>
                                    <option value="GT">Guatemala</option>
                                    <option value="GN">Guinea  </option>
                                    <option value="GW">Guinea-Bissau</option>
                                    <option value="GY">Guyana</option>
                                    <option value="HT">Haiti</option>
                                    <option value="HM">Heard </option>
                                    <option value="HN">Honduras</option>
                                    <option value="HK">Hong Kong</option>
                                    <option value="OT">Howland Island</option>
                                    <option value="HU">Hungary</option>
                                    <option value="IS">Iceland</option>
                                    <option value="IN">India</option>
                                    <option value="ID">Indonesia</option>
                                    <option value="IR">Iran</option>
                                    <option value="IQ">Iraq</option>
                                    <option value="IE">Ireland</option>
                                    <option value="IL">Isreal</option>
                                    <option value="IT">Italy</option>
                                    <option value="OT">Ivory Coast</option>
                                    <option value="JM">Jamaica</option>
                                    <option value="JP">Japan</option>
                                    <option value="OT">Johnston Atol</option>
                                    <option value="JO">Jordan</option>
                                    <option value="KZ">Kazakhstan</option>
                                    <option value="KE">Kenya</option>
                                    <option value="OT">Kingman Reef</option>
                                    <option value="KI">Kiribati</option>
                                    <option value="OT">Kosovo</option>
                                    <option value="KW">Kuwait</option>
                                    <option value="KG">Kyrgyzstan</option>
                                    <option value="LA">Lao</option>
                                    <option value="LV">Latvia</option>
                                    <option value="LB">Lebanon</option>
                                    <option value="LS">Lesotho</option>
                                    <option value="LR">Liberia</option>
                                    <option value="LY">Libya</option>
                                    <option value="LI">Liechtenstein</option>
                                    <option value="LT">Lithuania</option>
                                    <option value="LU">Luxembourg</option>
                                    <option value="MO">Macau</option>
                                    <option value="MK">Macedonia</option>
                                    <option value="MG">Madagascar</option>
                                    <option value="MW">Malawi</option>
                                    <option value="MY">Malaysia</option>
                                    <option value="MV">Maldives</option>
                                    <option value="ML">Mali</option>
                                    <option value="MT">Malta</option>
                                    <option value="OT">Malvinas (Islas)</option>
                                    <option value="OT">Maroc</option>
                                    <option value="MH">Marshall Islands</option>
                                    <option value="MQ">Martinique</option>
                                    <option value="MR">Mauritania</option>
                                    <option value="MU">Mauritius</option>
                                    <option value="YT">Mayotte</option>
                                    <option value="MX">Mexico</option>
                                    <option value="FM">Micronesia </option>
                                    <option value="OT">Midway Islands</option>
                                    <option value="MD">Moldova</option>
                                    <option value="MC">Monaco</option>
                                    <option value="MN">Mongolia</option>
                                    <option value="MS">Montserrat</option>
                                    <option value="MA">Morocco</option>
                                    <option value="MZ">Mozambiqe</option>
                                    <option value="MM">Myanmar</option>
                                    <option value="KR">N. Korea</option>
                                    <option value="NA">Namibia</option>
                                    <option value="NR">Nauru</option>
                                    <option value="NP">Nepal</option>
                                    <option value="NL">Netherlands</option>
                                    <option value="AN">Netherlands Antilles</option>
                                    <option value="NC">New Caledonia</option>
                                    <option value="NZ">New Zealand</option>
                                    <option value="NI">Nicaragua</option>
                                    <option value="NE">Niger</option>
                                    <option value="NG">Nigeria</option>
                                    <option value="NU">Niue</option>
                                    <option value="NF">Norfolk Island</option>
                                    <option value="KR">North Korea</option>
                                    <option value="MP">Northern Mariana Island</option>
                                    <option value="NO">Norway</option>
                                    <option value="OM">Oman</option>
                                    <option value="PK">Pakistan</option>
                                    <option value="PW">Palau</option>
                                    <option value="OT">Palestine</option>
                                    <option value="OT">Palmyra</option>
                                    <option value="PA">Panama</option>
                                    <option value="PG">Papua New Guinea</option>
                                    <option value="PY">Paraguay</option>
                                    <option value="OT">Parcel Islands </option>
                                    <option value="PE">Peru</option>
                                    <option value="PH">Phillipines</option>
                                    <option value="PN">Pitcairn</option>
                                    <option value="PL">Poland</option>
                                    <option value="PT">Portugal</option>
                                    <option value="PR">Puerto Rico</option>
                                    <option value="QA">Qatar</option>
                                    <option value="AE">Ra's al Khayman</option>
                                    <option value="RE">Reunion</option>
                                    <option value="RO">Romania</option>
                                    <option value="RU">Russia</option>
                                    <option value="RU">Russian Federation</option>
                                    <option value="RW">Rwanda</option>
                                    <option value="GS">S. Georgia</option>
                                    <option value="KP">S. Korea</option>
                                    <option value="AN">Saba</option>
                                    <option value="WS">Samoa</option>
                                    <option value="SM">San Marino</option>
                                    <option value="ST">Sao Tome and Principe</option>
                                    <option value="SA">Saudi Arabia</option>
                                    <option value="GB">Scotland</option>
                                    <option value="SN">Senegal</option>
                                    <option value="SC">Seychelles</option>
                                    <option value="AE">Sharjah</option>
                                    <option value="SL">Sierra Leone</option>
                                    <option value="SG">Singapore</option>
                                    <option value="SK">Slovakia</option>
                                    <option value="SI">Slovenia</option>
                                    <option value="SB">Solomon Islands</option>
                                    <option value="SO">Somalia</option>
                                    <option value="ZA">South Africa</option>
                                    <option value="KP">South Korea</option>
                                    <option value="ES">Spain</option>
                                    <option value="OT">Spratly Islands</option>
                                    <option value="LK">Sri Lanka</option>
                                    <option value="OT">St. Barthelemy</option>
                                    <option value="AN">St. Eustatius</option>
                                    <option value="SH">St. Helena</option>
                                    <option value="KN">St. Kitts and Nevis</option>
                                    <option value="LC">St. Lucia</option>
                                    <option value="AN">St. Maarten</option>
                                    <option value="PM">St. Pierre/Miquelon</option>
                                    <option value="VC">St. Vincent/Grenedine</option>
                                    <option value="SD">Sudan</option>
                                    <option value="SR">Suriname</option>
                                    <option value="SJ">Svalbard</option>
                                    <option value="SZ">Swaziland</option>
                                    <option value="SE">Sweden</option>
                                    <option value="CH">Switzerland</option>
                                    <option value="SY">Syria</option>
                                    <option value="TW">Taiwan</option>
                                    <option value="TJ">Tajikistan</option>
                                    <option value="TZ">Tanzania</option>
                                    <option value="TH">Thailand</option>
                                    <option value="TG">Togo</option>
                                    <option value="TK">Tokelau</option>
                                    <option value="TO">Tonga</option>
                                    <option value="TT">Trinidad  Tobago</option>
                                    <option value="TN">Tunisia</option>
                                    <option value="TR">Turkey</option>
                                    <option value="TM">Turkmenistan</option>
                                    <option value="TC">Turks </option>
                                    <option value="TV">Tuvalu</option>
                                    <option value="UG">Uganda</option>
                                    <option value="UA">Ukraine</option>
                                    <option value="AE">Umm al Qaywayn</option>
                                    <option value="AE">United Arab Emirates</option>
                                    <option value="GB">United Kingdom</option>
                                    <option value="UY">Uruguay</option>
                                    <option value="UZ">Uzbekistan</option>
                                    <option value="VU">Vanuatu</option>
                                    <option value="VA">Vatican City State</option>
                                    <option value="VE">Venezuela</option>
                                    <option value="VN">Vietnam</option>
                                    <option value="VG">Virgin Islands(British)</option>
                                    <option value="VI">Virgin Islands (US)</option>
                                    <option value="OT">Wake Island</option>
                                    <option value="GB">Wales</option>
                                    <option value="WF">Wallis </option>
                                    <option value="OT">West Africa</option>
                                    <option value="OT">West Indies</option>
                                    <option value="EH">Western Sahara</option>
                                    <option value="YE">Yemen</option>
                                    <option value="YU">Yugoslavia</option>
                                    <option value="ZM">Zambia</option>
                                    <option value="ZW">Zimbabwe</option>
                                    <option value="OT">OTHER</option>
                                </select>
                        </td></tr>

                        <tr><td>Occupation&nbsp;:</td><td><input type=text name=Occupation></td></tr>

                        <tr><td>Contact Number&nbsp;:</td><td><input type=text name=ContactNumber></td></tr>

                    </table>
                    <br>
                    <center><input type=submit value=submit class="but">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</center>
                    <input type=reset value=reset name=reset class="but">
                </form>
            </td>
        </tr>
    </table>
    </tr>
    </table></td>
</tr></table></BODY><table>
<tr><td background=images/title_back3.jpg width=1000 height=15></td></tr>
<%       } else {
%>
<body bgcolor=#B9CCEE><br><br><br><br>
    <center><table border=10 width=90%><tr><td  height=20% bgcolor=#9185C9 width=90%>
                    <h2><font color=#CCFFFF><center><br>
                    Only For Administrator Access<br></font></h2><center>
                    <img src='images/backbutton1.bmp' onClick=history.back()>
<br></td></tr></table></center></body>
<%            }
        }
%>