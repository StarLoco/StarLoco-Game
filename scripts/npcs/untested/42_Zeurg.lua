local npc = Npc(42, 9021)

npc.gender = 1

npc.sales = {
    {item=493},
    {item=700},
    {item=1334}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(60, {200, 198})
    elseif answer == 198 then p:ask(247, {199})
    elseif answer == 199 then p:ask(249)
    elseif answer == 200 then p:ask(251, {376})
    elseif answer == 376 then p:ask(449)
    end
end

RegisterNPCDef(npc)
