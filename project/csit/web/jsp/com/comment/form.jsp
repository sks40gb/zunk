<%@include file="/jsp/com/init.jsp" %>
<table align="center"  class="formTable">
    <tr>
        <td>SUBJECT</td>
        <td><input type=text name="<%=CommentModel.SUBJECT%>" ></td>
    </tr>
    <tr>
          <td>COMMENT</td>
        <td>
            <TEXTAREA name="<%=CommentModel.COMMENT%>" rows="10" cols="50"></TEXTAREA>
        </td>
    </tr>
</table>