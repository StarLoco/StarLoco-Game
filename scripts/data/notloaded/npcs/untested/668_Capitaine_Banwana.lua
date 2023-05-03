local npc = Npc(668, 120)

npc.colors = {7702408, 16777215, 3552822}
npc.accessories = {0, 699, 0, 0, 7085}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2739, {2358})
    elseif answer == 2358 then p:endDialog()
    end
end

RegisterNPCDef(npc)
