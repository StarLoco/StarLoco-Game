local npc = Npc(335, 111)

npc.gender = 1
npc.colors = {16777215, 16222121, 16375755}
npc.accessories = {0, 1907, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1319, {987, 98})
    elseif answer == 987 then p:ask(1324)
    end
end

RegisterNPCDef(npc)
