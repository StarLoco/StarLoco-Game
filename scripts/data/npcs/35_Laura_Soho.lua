local npc = Npc(35, 9012)

npc.gender = 1
npc.colors = {1252453, 15264239, 3349067}

npc.sales = {
    {item=311},
    {item=351}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(102, {143, 113, 112})
    elseif answer == 112 then p:ask(130)
    elseif answer == 113 then p:ask(131, {142})
    elseif answer == 142 then p:ask(168)
    elseif answer == 143 then p:ask(169)
    end
end

RegisterNPCDef(npc)
