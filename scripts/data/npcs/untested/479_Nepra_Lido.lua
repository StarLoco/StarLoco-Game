local npc = Npc(479, 71)

npc.gender = 1
npc.colors = {12568056, 12722730, 13869725}
npc.accessories = {0, 0, 935, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2248, {2131})
    elseif answer == 2131 then p:endDialog()
    end
end

RegisterNPCDef(npc)
