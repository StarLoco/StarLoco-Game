local npc = Npc(42, 9021)
--TODO: NPC Temple Osa > Check dialogs offi en étant Osa voir si ça lance une quuête
npc.sales = {
    {item=493},
    {item=700},
    {item=1334}
}

npc.barters = {
    {to={itemID=802, quantity= 1}, from= {
        {itemID=383, quantity= 100}
    }},
    {to={itemID=493, quantity= 1}, from= {
        {itemID=383, quantity= 1}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(60, {198, 200})
    elseif answer == 198 then
        if p:breed() == 2 then p:ask(247, {199})
        else  p:ask(248)
        end
    elseif answer == 199 then p:ask(249)
    elseif answer == 200 then p:ask(251, {376})
    elseif answer == 376 then p:ask(449)
    end
end

RegisterNPCDef(npc)
