package org.starloco.locos.script.proxy;

import org.classdump.luna.Table;
import org.classdump.luna.impl.DefaultTable;
import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.script.types.MetaTables;

public class SAccount extends DefaultUserdata<Account> {
    private static final ImmutableTable META_TABLE = MetaTables.MetaTable(MetaTables.ReflectIndexTable(SAccount.class));

    public SAccount(Account userValue) {
        super(META_TABLE, userValue);
    }

    @SuppressWarnings("unused")
    private static int id(Account a) {
        return a.getId();
    }

    @SuppressWarnings("unused")
    private static Table friends(Account a) {
        DefaultTable table = new DefaultTable();
        a.getFriendIds().forEach(fId -> {
            table.rawset(table.rawlen()+1, fId);
        });
        return table;
    }
}
