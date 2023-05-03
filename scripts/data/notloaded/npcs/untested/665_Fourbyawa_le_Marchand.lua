local npc = Npc(665, 120)

npc.colors = {15514699, 15813709, 16435460}
npc.accessories = {0, 6921, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2736)
    end
end

RegisterNPCDef(npc)
