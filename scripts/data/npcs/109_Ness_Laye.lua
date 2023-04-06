local npc = Npc(109, 9042)

npc.gender = 1
npc.colors = {16312467,-1,-1}
npc.barters = {
    -- '757:60,369:20,368:25|808:1'
    {to={itemID=808, quantity= 1},from= {
        {itemID=368, quantity= 25},
        {itemID=369, quantity= 20},
        {itemID=757, quantity= 60},
    }}
}

function npc:onTalk(p, answer)
    p:ask(364, {})
end

RegisterNPCDef(npc)
