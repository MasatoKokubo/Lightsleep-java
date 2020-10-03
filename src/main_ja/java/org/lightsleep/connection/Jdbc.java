// Jdbc.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import javax.sql.DataSource;

import org.lightsleep.helper.Resource;

/**
 * <b>JdbcConnection</b>は、<b>java.sql.DriverManager</b>クラスから直接
 * コネクションラッパーを取得する場合に使用します。<br>
 * lightsleep.propertiesファイルの以下のプロパティを参照します。<br>
 *
 * <div class="blankline">&nbsp;</div>
 *
 * <table class="additional">
 *   <caption><span>lightsleep.propertiesの参照</span></caption>
 *   <tr><th>プロパティ名</th><th>内 容</th></tr>
 *   <tr><td>url     </td><td>接続するデータベースの URL</td></tr>
 *   <tr><td>user    </td><td>データベースに接続する時のユーザー名</td></tr>
 *   <tr><td>password</td><td>データベースに接続する時のパスワード</td></tr>
 *   <tr>
 *     <td><i>その他のプロパティ名</i></td>
 *     <td><b>DriverManager</b>からコネクションラッパーを取得する時に使用するその他のプロパティ</td>
 *   </tr>
 * </table>
 *
 * @since 1.1.0
 * @author Masato Kokubo
 */
public class Jdbc extends AbstractConnectionSupplier {
    /**
     * <b>Jdbc</b>を構築します。
     *
     * <p>
     * lightsleep.propertiesファイルで指定された値を設定情報として使用します。
     * </p>
     */
    public Jdbc() {
        super(Resource.getGlobal().getProperties(), modifier -> {});
    }

    @Override
    public DataSource getDataSource() {
        return null;
    }
}
