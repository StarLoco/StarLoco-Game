local npc = Npc(810, 90)

npc.colors = {0, 4194304, 16711910}
npc.accessories = {2416, 2411, 2414, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3353, {2987, 2988})
    elseif answer == 2987 then p:endDialog()
    elseif answer == 2988 then p:endDialog()
    end
end

RegisterNPCDef(npc)
